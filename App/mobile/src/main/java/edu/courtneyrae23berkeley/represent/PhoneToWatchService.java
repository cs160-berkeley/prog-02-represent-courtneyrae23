package edu.courtneyrae23berkeley.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

/**
 * Created by court_000 on 2/28/2016.
 */
public class PhoneToWatchService extends Service {

    private GoogleApiClient mApiClient;
    ArrayList<String> repNames;
    ArrayList<String> parties;
    ArrayList<String> repNums;
    ArrayList<String> rep_sen;
    String votes;
    String location;
    String stateVotes;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Which cat do we want to feed? Grab this info from INTENT
        // which was passed over when we called startService

        Bundle b = intent.getExtras();
        repNames = b.getStringArrayList("repNames");
        repNums = b.getStringArrayList("repNums");
        rep_sen = b.getStringArrayList("Rep_Sen");
        parties = b.getStringArrayList("parties");
        location = b.getString("location");
        votes = b.getString("votes");
        stateVotes = b.getString("stateVotes");
        Log.d("T", "in PhoneToWatch!!");

        // Send the message with the cat name
        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();
                String repDel = "";
                String partyDel = "";
                String repNumDel = "";
                String repOrSen = "";
                for (int i = 0; i < repNames.size(); i++) {
                    repDel = repDel + repNames.get(i) + ",";
                }
                sendMessage("/sendName", repDel);

                for (int i = 0; i < rep_sen.size(); i++) {
                    repOrSen = repOrSen + rep_sen.get(i) + ",";
                }
                sendMessage("/sendRepSen", repOrSen);

                for (int i = 0; i < parties.size(); i++) {
                    partyDel = partyDel + parties.get(i) + ",";
                }
                sendMessage("/sendParty", partyDel);

                for (int i = 0; i < repNums.size(); i++) {
                    repNumDel = repNumDel + repNums.get(i) + ",";
                }
                sendMessage("/sendNums", repNumDel);

                sendMessage("/sendVotes", votes);

                sendMessage("/sendStateVotes", stateVotes);

                sendMessage("/location", location);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMessage("/startMain", "");
            }
        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBinder
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }

}
