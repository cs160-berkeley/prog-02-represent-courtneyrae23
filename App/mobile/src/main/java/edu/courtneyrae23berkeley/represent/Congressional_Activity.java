package edu.courtneyrae23berkeley.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

/**
 * Created by court_000 on 2/24/2016.
 */
public class Congressional_Activity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //What we need to fill out to pass along
    String state_details;
    ArrayList<String> repNames = new ArrayList<String>();
    ArrayList<String> repOrSen = new ArrayList<>();
    ArrayList<String> parties = new ArrayList<String>();
    ArrayList<String> repNums = new ArrayList<>();
    ArrayList<String> webLinks = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();
    ArrayList<String> facebooks = new ArrayList<>();
    ArrayList<String> twitter = new ArrayList<>();
    double obama;
    double romney;
    Bitmap rep_picture;
    ArrayList<Bitmap> images;

    boolean isZipCode;
    String zip;
    private Context context;
    TableLayout myLayout;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "oyVupTpHO3kVQYK6dahhgwEJn";
    private static final String TWITTER_SECRET = "bNMKaQhZjjJP1DsYJYXSPsaU4Wn1SPzP3ctB1BbxhaiUEAd803";
    private GoogleApiClient mGoogleApiClient;
    String jsonString;
    JSONArray jArray;


    Location mLastLocation;
    private static final String GOOGLE_GEOCODE_KEY = "AIzaSyASnVBnVHxycKxWQHTQTEbHumdpx3-KsCw";
    private static final String SUNLIGHT_KEY = "2f26a71e3a3b4f0fa8afd6c469b542e0";

    double lat;
    double lng;
    String county_name;
    String state;
    String obama_state;
    String romney_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.congressional);
        context = this;


        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();


        Intent in = getIntent();
        Bundle b = in.getExtras();

        String message = b.getString("MessageType");
        Log.d("T", "got the message.....");

        if (message.equals("shake")) {
            Log.d("T", "got the message! Shake!");
            isZipCode = true;

            InputStream inputStream = null;
            try {
                inputStream = getAssets().open("zipcodes.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("T", "Got input stream" + inputStream);

            LineNumberReader rdr = new LineNumberReader(new InputStreamReader(inputStream));
            int numLines = 42161;
            Random r = new Random();
            int randInt = r.nextInt(numLines);
            rdr.setLineNumber(randInt);
            try {
                int i = 0;
                while (i < randInt) {
                    rdr.readLine();
                    i++;
                }
                String temp = rdr.readLine();
                zip = temp.replaceAll("\\s+","");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("T", "New zip " + zip);


            new CountyInfo().execute(zip);
            new LegislatorsInfo().execute(zip);


        } else if (message.equals("start")) {
            Log.d("T", "got the message! Start!");
            isZipCode = b.getBoolean("zipcode");
            zip = b.getString("ZIP");
            if (isZipCode) {
                new CountyInfo().execute(zip);
                new LegislatorsInfo().execute(zip);
            }
        }

        //Call Google's Location thing to go from lat, long or Zipcode -> County

        //Get myCounty from ZIP/Current Loc and in post, get obama/romney data


        //Call Sunlight's Thing to get all of the representatives for zipcode/location

//
//
//        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//

//
//        sendIntent.putExtra("repNames", repNames);
//        sendIntent.putExtra("parties", parties);
//      //  startService(sendIntent);
    }


    public void initiatePopUpWindow(View view) {

        int tag = (Integer) view.getTag();
        final String twitter_id = twitter.get(tag);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, twitter_id, 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {
                        for (Tweet tweet : listResult.data) {
                            TweetView tweetView = new TweetView(Congressional_Activity.this, tweet);
                            PopupWindow myPopupWindow = new PopupWindow(tweetView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                            myPopupWindow.setOutsideTouchable(true);
                            myPopupWindow.setFocusable(true);
                            myPopupWindow.setBackgroundDrawable(new ColorDrawable(0x80000000));
                            myPopupWindow.showAtLocation(tweetView, Gravity.CENTER, 0, 0);
                        }
                    }
                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    public void detailedclick(View view) {
        Intent intent = new Intent(Congressional_Activity.this, Detailed_Activity.class);
        int tag = ((Integer) view.getTag());
        intent.putExtra("id", repNums.get(tag));
        startActivity(intent);
    }

    public void linkClick(View view) {
        Uri uri;
        Intent intent;
        int tag = (Integer) view.getTag();
        uri = Uri.parse(webLinks.get(tag));
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void emailClick(View view) {
        Uri uri;
        Intent intent;
        int tag = (Integer) view.getTag();
        uri = Uri.parse("mailto:" + emails.get(tag));
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Congressional_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.courtneyrae23berkeley.represent/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Congressional_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.courtneyrae23berkeley.represent/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
        }
        if (!isZipCode) {
            new CountyInfo().execute(String.valueOf(lat), String.valueOf(lng));
            new LegislatorsInfo().execute(String.valueOf(lat), String.valueOf(lng));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {
    }


    private class LegislatorsInfo extends AsyncTask<String, String, JSONArray> {

        private String resp;
        private JSONArray results;

        @Override
        protected JSONArray doInBackground(String... location) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            String API_URL = "https://congress.api.sunlightfoundation.com/legislators/locate?";
            if (isZipCode) {
                try {
                    URL url = new URL(API_URL + "zip=" + location[0] + "&apikey=" + SUNLIGHT_KEY);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        String myString = stringBuilder.toString();
                        Log.d("T", "connected to" + myString);
                        resp = myString;
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            } else {
                try {
                    URL url = new URL(API_URL + "latitude=" + location[0] + "&longitude=" + location[1] + "&apikey=" + SUNLIGHT_KEY);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        String myString = stringBuilder.toString();
                        Log.d("T", "connected to" + myString);
                        resp = myString;
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            }
            try {
                JSONObject jObject = new JSONObject(resp);
                results = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            Log.d("T", "I am called");
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject rep = result.getJSONObject(i);
                    repNames.add(rep.getString("first_name") + " " + rep.getString("last_name"));
                    repNums.add(rep.getString("bioguide_id"));
                    parties.add(rep.getString("party"));
                    webLinks.add(rep.getString("website"));
                    emails.add(rep.getString("oc_email"));
                    facebooks.add(rep.getString("facebook_id"));
                    twitter.add(rep.getString("twitter_id"));
                    repOrSen.add(rep.getString("chamber"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            TextView zipCodeMode = (TextView) findViewById(R.id.zipCodeTitle);
            TextView stateLoc = (TextView) findViewById(R.id.stateTitle);

            if (isZipCode) {
                zipCodeMode.setText("ZIP Code: " + zip);
                stateLoc.setText(state_details);
            } else {
                zipCodeMode.setText("Current Location");
                stateLoc.setText(state_details);
            }

            // First pass deals with senators and inflating their view
            myLayout = (TableLayout) findViewById(R.id.tableLayout);

            TableRow senTitle = (TableRow) View.inflate(context, R.layout.title_row, null);
            TextView senText = (TextView) senTitle.findViewById(R.id.senRepTitle);
            senText.setText("Senators");
            myLayout.addView(senTitle);

            boolean first_call = false;
            for (int i = 0; i < repNames.size(); i++) {
                if (repOrSen.get(i).equals("senate")) {
                    new getPicture().execute(repNums.get(i), String.valueOf(i), String.valueOf(first_call));
                }
            }

            first_call = true;
            for (int i = 0; i < repNames.size(); i++) {
                if (repOrSen.get(i).equals("house")) {
                    new getPicture().execute(repNums.get(i), String.valueOf(i), String.valueOf(first_call));
                    first_call = false;
                }
            }
        }
    }

    private class getPicture extends AsyncTask<String, String, String[]> {

        private Bitmap img;

        @Override
        protected String[] doInBackground(String... id) {

            String API_URL = "https://theunitedstates.io/images/congress/450x550/";
            try {
                URL url = new URL(API_URL + id[0] + ".jpg");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    img = BitmapFactory.decodeStream(in);
                    Log.d("T", "Got image" + img);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }

            rep_picture = img;
            return new String[] {id[1], id[2]};
        }

        @Override
        protected void onPostExecute(String[] items) {

            int i = Integer.valueOf(items[0]);

            if (items[1].equals("true")) {
                TableRow repTitle = (TableRow) View.inflate(context, R.layout.title_row, null);
                TextView repText = (TextView) repTitle.findViewById(R.id.senRepTitle);
                repText.setText("Representatives");
                 myLayout.addView(repTitle);
            }

            TableRow senator = (TableRow) View.inflate(context, R.layout.congress_row, null);
            Button senName = (Button) senator.findViewById(R.id.name);
            senName.setTag(i);
            TableRow background = (TableRow) senator.findViewById(R.id.row_background);
            ImageButton picture = (ImageButton) senator.findViewById(R.id.picture);
            picture.setTag(i);

            Log.d("T", "Got image pic " + rep_picture);
            picture.setImageBitmap(rep_picture);

            Button website = (Button) senator.findViewById(R.id.website);
            website.setTag(i);
            Button moreInfo = (Button) senator.findViewById(R.id.moreinfo);
            moreInfo.setTag(i);
            Button email = (Button) senator.findViewById(R.id.email);
            email.setTag(i);
            Button twitter = (Button) senator.findViewById(R.id.tweet);
            twitter.setTag(i);

            if (parties.get(i).equals("R")) {
                senName.setText(repNames.get(i) + " (R)");
                background.setBackgroundResource(R.drawable.outline);
                //background.setBackgroundColor(Color.parseColor("#E63939"));
            } else if (parties.get(i).equals("D")) {
                senName.setText(repNames.get(i) + " (D)");
                background.setBackgroundResource(R.drawable.outline2);
                //background.setBackgroundColor(Color.parseColor("#4571CF"));
            }
            myLayout.addView(senator);
        }
    }

    private class CountyInfo extends AsyncTask<String, String, String> {

        private String resp;
        String lat;
        String lng;

        @Override
        protected String doInBackground(String... location) {
            String ZIPCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
            String API_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
            if (isZipCode) {
                // Get lat and long from ZipCode first!!!
                try {
                    URL url = new URL(ZIPCODE_URL + zip + "&sensor=false&key=" + GOOGLE_GEOCODE_KEY);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        String myString = stringBuilder.toString();
                        resp = myString;

                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
                try {
                    JSONObject latLongResults = new JSONObject(resp);
                    JSONArray results = latLongResults.getJSONArray("results");
                    JSONObject actual_results = results.getJSONObject(0);
                    JSONObject geometry = actual_results.getJSONObject("geometry");
                    JSONObject latLong = geometry.getJSONObject("location");
                    lat = latLong.getString("lat");
                    lng = latLong.getString("lng");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                lat = location[0];
                lng = location[1];
            }

            try {
                URL url = new URL(API_URL + lat + "," + lng + "&key=" + GOOGLE_GEOCODE_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String myString = stringBuilder.toString();
                    try {
                        JSONObject countyResults = new JSONObject(myString);
                        JSONArray results = countyResults.getJSONArray("results");
                        JSONObject actual_results = results.getJSONObject(0);
                        JSONArray address = actual_results.getJSONArray("address_components");

                        JSONObject cityObject = new JSONObject();
                        JSONObject stateObject = new JSONObject();
                        JSONObject countyObject = new JSONObject();

                        for (int i=0; i < address.length(); i++) {
                            JSONObject newObj = address.getJSONObject(i);
                            JSONArray types = newObj.getJSONArray("types");
                            if (types.length() > 1) {
                                if ((types.getString(0)).equals("locality")) {
                                    cityObject = address.getJSONObject(i);
                                } else if ((types.getString(0)).equals("administrative_area_level_2")) {
                                    countyObject = address.getJSONObject(i);
                                } else if ((types.getString(0)).equals("administrative_area_level_1")) {
                                    stateObject = address.getJSONObject(i);
                                }
                            }
                            Log.d("T", "What's in types: " + types);
                        }

                        state = stateObject.getString("short_name");
                        state_details = cityObject.getString("long_name") + ", " + state;
                        String[] county = countyObject.getString("long_name").split(" County");
                        county_name = county[0];

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
            return county_name;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            try {
                InputStream stream = getAssets().open("election-county-2012.json");
                int size = stream.available();
                byte[] buffer = new byte[size];
                stream.read(buffer);
                stream.close();
                jsonString = new String(buffer, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                jArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String county = oneObject.getString("county-name");
                    if (county.equals(result)) {
                        obama = oneObject.getDouble("obama-percentage");
                        romney = oneObject.getDouble("romney-percentage");
                    }
                } catch (JSONException e) {
                    // Oops
                }
            }

            new StateVote().execute(state);
            Log.d("T", "connected....." + obama + "," + romney);
        }

    }

    private class StateVote extends AsyncTask<String, String, JSONObject> {
        String resp;
        JSONObject myChart;

        @Override
        protected JSONObject doInBackground(String... location) {
            String API_URL = "http://elections.huffingtonpost.com/pollster/api/charts.json?state=";
            // Get lat and long from ZipCode first!!!
            try {
                URL url = new URL(API_URL + state);
                Log.d("T", "I am called " + url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String myString = stringBuilder.toString();
                    resp = myString;
                    Log.d("T", "Got state data" + resp);

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
            try {
                JSONArray results = new JSONArray(resp);
                Log.d("T", "Got state data" + results);
                for (int i = 0; i < results.length(); i++) {
                    JSONObject chart = results.getJSONObject(i);
                    if (chart.getString("topic").equals("2012-president")) {
                        myChart = chart;
                        break;
                    }
                }
                Log.d("T", "Got state data" + results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return myChart;
        }


        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                JSONArray estimates = result.getJSONArray("estimates");
                for (int i = 0; i < estimates.length(); i++) {
                    JSONObject obj = estimates.getJSONObject(i);
                    if (obj.getString("choice").equals("Obama")) {
                        obama_state = obj.getString("value");
                    } else if (obj.getString("choice").equals("Romney")) {
                        romney_state = obj.getString("value");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("T", "I get " + obama_state + "," + romney_state);

            Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            sendIntent.putExtra("repNames", repNames);
            sendIntent.putExtra("parties", parties);
            sendIntent.putExtra("repNums", repNums);
            sendIntent.putExtra("votes", obama + "," + romney);
            sendIntent.putExtra("Rep_Sen", repOrSen);
            sendIntent.putExtra("location", county_name + " County");
            sendIntent.putExtra("stateVotes", state + "," + obama_state + "," + romney_state);
            startService(sendIntent);

        }
    }
}

