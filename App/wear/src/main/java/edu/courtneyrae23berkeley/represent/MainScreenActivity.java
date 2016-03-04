package edu.courtneyrae23berkeley.represent;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainScreenActivity extends WearableActivity implements SensorEventListener {

    private float initialX, initialY, finalX, finalY;
    static final int MIN_DISTANCE = 150;
    ArrayList<String> repNames;
    ArrayList<String> parties;
    int totalReps;
    int currentRep;
    final int THRESHOLD = 80;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    String location;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        Log.d("T", "new MainActivity");

        repNames = extras.getStringArrayList("repNames");
        parties = extras.getStringArrayList("parties");
        totalReps = repNames.size();
        currentRep = extras.getInt("currentRep");
        location = extras.getString("location");


        TextView repName = (TextView) findViewById(R.id.repName);
        repName.setText(repNames.get(currentRep));

        TextView repParty = (TextView) findViewById(R.id.party);
        repParty.setText(parties.get(currentRep));

        if (currentRep >= 2) {
            TextView rep = (TextView) findViewById(R.id.repOrSen);
            String repWithDot = "Representative    " +  "\u00b7" + " ";
            rep.setText(repWithDot);
            rep.setTextSize(16);
            rep.setPadding(10,50,0,0);

            repParty.setTextSize(16);
            repParty.setPadding(0,50,10,0);
        } else {
            TextView rep = (TextView) findViewById(R.id.repOrSen);
            String senWithDot = "Senator    " +  "\u00b7" + " ";
            rep.setText(senWithDot);
        }

        View view = findViewById(R.id.main);
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getActionMasked();
                Bundle b = new Bundle();
                b.putStringArrayList("repNames", repNames);
                b.putStringArrayList("parties", parties);
                b.putString("location", location);

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        finalX = event.getX();
                        finalY = event.getY();
                        if (initialX < finalX && finalX - initialX > MIN_DISTANCE) {
                            //left to right
                            //go back to previous screen
                            if (currentRep == 0) {
                                //donothing
                            } else {
                                b.putInt("currentRep", currentRep - 1);
                                Intent screenSwitch = new Intent(MainScreenActivity.this, MainScreenActivity.class);
                                screenSwitch.putExtras(b);
                                startActivity(screenSwitch);
                            }

                        } else if (initialX > finalX && initialX - finalX > MIN_DISTANCE) {
                            //right to left
                            //go to next Rep
                            if (currentRep == totalReps - 1) {
                                //donothing
                            } else {
                                b.putInt("currentRep", currentRep + 1);
                                Intent screenSwitch = new Intent(MainScreenActivity.this, MainScreenActivity.class);
                                screenSwitch.putExtras(b);
                                startActivity(screenSwitch);
                            }
                        } else if (initialY > finalY && initialY - finalY > MIN_DISTANCE) {
                            //down to up
                            //go down to voter view
                            b.putInt("currentRep", currentRep);
                            b.putString("currentLocation", location);
                            Intent screenSwitch = new Intent(MainScreenActivity.this, VoterActivity.class);
                            screenSwitch.putExtras(b);
                            startActivity(screenSwitch);
                        } else {
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            Bundle b2 = new Bundle();
                            b2.putString("repName", repNames.get(currentRep));
                            b2.putString("party", parties.get(currentRep));
                            b2.putString("MessageType", "detailed");
                            sendIntent.putExtras(b2);
                            startService(sendIntent);
                            //it's a tap
                        }
                        break;
                }
                return true;
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onAccuracyChanged(Sensor s, int num) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (x > THRESHOLD || y > THRESHOLD || z > THRESHOLD) {
            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
            Bundle b = new Bundle();
            b.putString("MessageType", "shake");
            sendIntent.putExtras(b);
            startService(sendIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "WatchMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.courtneyrae23berkeley.represent/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "WatchMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.courtneyrae23berkeley.represent/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

