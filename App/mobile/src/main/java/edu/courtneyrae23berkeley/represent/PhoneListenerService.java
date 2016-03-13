package edu.courtneyrae23berkeley.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by court_000 on 2/28/2016.
 */
public class PhoneListenerService extends WearableListenerService {

    String id;

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String DETAILS = "/detailed";
    private static final String SHAKE = "/shake";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(DETAILS) ) {
            String id = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, Detailed_Activity.class );
            Log.d("T", "Got: " + id);
            intent.putExtra("id", id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            Log.d("T", "about to start detailed activity");
            startActivity(intent);

        } else if( messageEvent.getPath().equalsIgnoreCase(SHAKE) ) {
            Intent intent = new Intent(this, Congressional_Activity.class);
            intent.putExtra("MessageType", "shake");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            Log.d("T", "about to restart with new shake locations");
            startActivity(intent);

        } else if (messageEvent.getPath().equalsIgnoreCase("/currentVote")) {
            String state = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, Current_Election_Activity.class);
            intent.putExtra("state", state);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            Log.d("T", "about to get current votes");
            startActivity(intent);

        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}

