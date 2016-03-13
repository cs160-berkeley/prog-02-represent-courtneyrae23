package edu.courtneyrae23berkeley.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainScreenActivity extends WearableActivity implements SensorEventListener {

    private float initialX, initialY, finalX, finalY;
    static final int MIN_DISTANCE = 150;
    ArrayList<String> repNames;
    ArrayList<String> repNums;
    ArrayList<String> parties;
    ArrayList<String> repOrSen;
    String obama;
    String romney;
    int totalReps;
    int currentRep;
    final int THRESHOLD = 80;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    String party;
    String location;
    String state;
    String obama_state;
    String romney_state;
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
        repOrSen = extras.getStringArrayList("repOrSen");
        location = extras.getString("location");
        repNums = extras.getStringArrayList("repNums");
        obama = extras.getString("obama");
        romney = extras.getString("romney");
        state = extras.getString("state");
        obama_state = extras.getString("obama_state");
        romney_state = extras.getString("romney_state");


        TextView repName = (TextView) findViewById(R.id.repName);
        repName.setText(repNames.get(currentRep));


        LinearLayout background_layout = (LinearLayout) findViewById(R.id.main);

        if (parties.get(currentRep).equals("D")) {
            background_layout.setBackgroundColor(Color.parseColor("#4571CF"));
            party = "Democrat";
        } else if (parties.get(currentRep).equals("R")) {
            party = "Republican";
            background_layout.setBackgroundColor(Color.parseColor("#E63939"));
        }

        if (repOrSen.get(currentRep).equals("house")) {
            TextView rep = (TextView) findViewById(R.id.RepSenParty);
            String repWithDot = "Representative " +  "\u00b7" + " " + party ;
            rep.setText(repWithDot);
        } else {
            TextView rep = (TextView) findViewById(R.id.RepSenParty);
            String senWithDot = "Senator " +  "\u00b7" + " " + party;
            rep.setText(senWithDot);
        }

        View view = findViewById(R.id.main);

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getActionMasked();
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
                            Intent screenSwitch = new Intent(MainScreenActivity.this, VoterActivity.class);
                            screenSwitch.putExtras(b);
                            startActivity(screenSwitch);
                        } else {
                            Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                            Bundle b2 = new Bundle();
                            b2.putString("id", repNums.get(currentRep));
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
    }

    @Override
    public void onAccuracyChanged(Sensor s, int num) {

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
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


//    private class getPicture extends AsyncTask<String, String, String> {
//
//        private Bitmap rep_picture;
//
//        @Override
//        protected String doInBackground(String... id) {
//
//            String API_URL = "https://theunitedstates.io/images/congress/450x550/";
//            try {
//                URL url = new URL(API_URL + id[0] + ".jpg");
//                Log.d("T", "Watch Got url" + url);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                try {
//                    InputStream in = urlConnection.getInputStream();
//                    rep_picture = BitmapFactory.decodeStream(in);
//                    Log.d("T", "Watch Got image" + rep_picture);
//                } finally {
//                    urlConnection.disconnect();
//                }
//            } catch (Exception e) {
//                Log.e("ERROR", e.getMessage(), e);
//            }
//            return id[0];
//        }
//
//        @Override
//        protected void onPostExecute(String item) {
//            LinearLayout background_layout = (LinearLayout) findViewById(R.id.main);
//            BitmapDrawable background = new BitmapDrawable(getResources(), rep_picture);
//            background_layout.setBackground(background);
//        }
//    }

}

