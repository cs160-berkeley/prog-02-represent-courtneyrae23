package edu.courtneyrae23berkeley.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by court_000 on 2/28/2016.
 */
public class VoterActivity extends Activity implements SensorEventListener{
    private float finalY;
    private float initialY;
    private float initialX;
    private float finalX;
    private int MIN_DISTANCE = 150;
    int totalReps;
    int currentRep;
    ArrayList<String> repNums;
    ArrayList<String> repOrSen;
    ArrayList<String> repNames;
    ArrayList<String> parties;
    String location;
    String obama;
    String romney;
    String state;
    String obama_state;
    String romney_state;
    final int THRESHOLD = 80;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voter);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        repNums = extras.getStringArrayList("repNums");
        repOrSen = extras.getStringArrayList("repOrSen");
        repNames = extras.getStringArrayList("repNames");
        parties = extras.getStringArrayList("parties");
        totalReps = repNames.size();
        currentRep = extras.getInt("currentRep");
        location = extras.getString("location");
        obama = extras.getString("obama");
        romney = extras.getString("romney");
        obama_state = extras.getString("obama_state");
        romney_state = extras.getString("romney_state");
        state = extras.getString("state");

        View view = findViewById(R.id.vote);

        if (repOrSen.get(currentRep).equals("senate")) {
            TextView loc = (TextView) findViewById(R.id.location);
            loc.setText(state);

            TextView obama_val = (TextView) findViewById(R.id.obama_perc);
            obama_val.setText(obama_state + "%");

            TextView romney_val = (TextView) findViewById(R.id.romney_perc);
            romney_val.setText(romney_state + "%");
        } else {
            TextView loc = (TextView) findViewById(R.id.location);
            loc.setText(location);
            TextView obama_val = (TextView) findViewById(R.id.obama_perc);
            obama_val.setText(obama + "%");

            TextView romney_val = (TextView) findViewById(R.id.romney_perc);
            romney_val.setText(romney + "%");
        }

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getActionMasked();
                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialY = event.getY();
                        initialX = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        finalY = event.getY();
                        finalX = event.getX();
                        if (initialX < finalX && finalX - initialX > MIN_DISTANCE) {
                            //left to right
                        } else if (initialX > finalX && initialX - finalX > MIN_DISTANCE) {
                            //right to left
                        } else if (initialY > finalY && initialY - finalY > MIN_DISTANCE) {
                            //down to up
                        } else if (initialY < finalY && finalY - initialY > MIN_DISTANCE) {
                            //up to down
                            //go up to main view
                            Bundle b = new Bundle();
                            b.putStringArrayList("repNames", repNames);
                            b.putStringArrayList("repNums", repNums);
                            b.putStringArrayList("repOrSen", repOrSen);
                            b.putStringArrayList("parties", parties);
                            b.putString("location", location);
                            b.putString("obama", obama);
                            b.putString("romney", romney);
                            b.putString("state", state);
                            b.putString("obama_state", obama_state);
                            b.putString("romney_state", romney_state);
                            b.putInt("currentRep", currentRep);
                            Intent screenSwitch = new Intent(VoterActivity.this, MainScreenActivity.class);
                            screenSwitch.putExtras(b);
                            startActivity(screenSwitch);
                        } else {
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            Bundle b2 = new Bundle();
                            b2.putString("state", state);
                            b2.putString("MessageType", "currentVote");
                            sendIntent.putExtras(b2);
                            startService(sendIntent);
                            //it's a tap
                        }

                        break;
                }
                return true;
            }
        });

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

    boolean currently_shaking = false;

    @Override
    public final void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (x > THRESHOLD || y > THRESHOLD || z > THRESHOLD) {
            currently_shaking = true;
            return;
        } else {
            if (currently_shaking) {
                currently_shaking = false;
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                Bundle b = new Bundle();
                b.putString("MessageType", "shake");
                sendIntent.putExtras(b);
                startService(sendIntent);
            }
            currently_shaking = false;
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
