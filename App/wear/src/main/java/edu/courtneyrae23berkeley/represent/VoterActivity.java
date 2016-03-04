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
    private int MIN_DISTANCE = 150;
    int totalReps;
    int currentRep;
    ArrayList<String> repNames;
    ArrayList<String> parties;
    String location;
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

        repNames = extras.getStringArrayList("repNames");
        parties = extras.getStringArrayList("parties");
        totalReps = repNames.size();
        currentRep = extras.getInt("currentRep");
        location = extras.getString("location");

        View view = findViewById(R.id.vote);
        TextView loc = (TextView) findViewById(R.id.location);
        loc.setText(location);

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getActionMasked();
                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        finalY = event.getY();

                        if (initialY < finalY && finalY - initialY > MIN_DISTANCE) {
                            //up to down
                            //go up to main view
                            Bundle b = new Bundle();
                            b.putStringArrayList("repNames", repNames);
                            b.putStringArrayList("parties", parties);
                            b.putString("location", location);
                            b.putInt("currentRep", currentRep);
                            Intent screenSwitch = new Intent(VoterActivity.this, MainScreenActivity.class);
                            screenSwitch.putExtras(b);
                            startActivity(screenSwitch);
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
