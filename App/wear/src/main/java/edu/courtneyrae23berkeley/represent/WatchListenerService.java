package edu.courtneyrae23berkeley.represent;

import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by court_000 on 2/28/2016.
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages

    ArrayList<String> repNames = new ArrayList<String>();
    ArrayList<String> parties = new ArrayList<String>();
    String location;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        //(here, fred vs lexy)

        if (messageEvent.getPath().equalsIgnoreCase("/sendName")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "in WatchListenerService, got: " + value);
            String[] tokens = value.split(",");
            for (int i = 0; i < tokens.length; i++) {
                repNames.add(tokens[i]);
            }
        } else if (messageEvent.getPath().equalsIgnoreCase("/location")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d("T", "in WatchListenerService, got: " + value);
            location = value;
        } else if (messageEvent.getPath().equalsIgnoreCase("/sendParty")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String[] tokens = value.split(",");
            for (int i = 0; i < tokens.length; i++) {
                parties.add(tokens[i]);
            }
        } else if (messageEvent.getPath().equalsIgnoreCase("/startMain")) {
            //String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainScreenActivity.class );
            intent.putExtra("repNames", repNames);
            intent.putExtra("parties", parties);
            intent.putExtra("currentRep", 0);
            intent.putExtra("location", location);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            Log.d("T", "about to start watch MainActivity");
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
