package edu.courtneyrae23berkeley.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by court_000 on 2/28/2016.
 */
public class Detailed_Activity extends Activity {

    String[] bill_names = new String[10];
    String[] bill_dates = new String[10];
    ArrayList<String> committee_names = new ArrayList<String>();
    String party;
    String id;
    String facebook;
    String email;
    String twitter;
    String website;
    String end_of_term;
    private Context context;
    private static final String SUNLIGHT_KEY = "2f26a71e3a3b4f0fa8afd6c469b542e0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.detailed);
        context = this;

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        Log.d("T", "My bundle " + b);

        id = b.getString("id");

        Log.d("T", "I recieved " + id);

        new LegislatorsInfo().execute(id);
        new getPicture().execute(id);
    }

    private class Committees extends AsyncTask<String, String, JSONArray> {

        private String resp;
        private JSONArray results;

        @Override
        protected JSONArray doInBackground(String... location) {
            String API_URL = "https://congress.api.sunlightfoundation.com/committees?member_ids=";
            try {
                URL url = new URL(API_URL + id + "&apikey=" + SUNLIGHT_KEY);
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
                JSONObject committeeResults = new JSONObject(resp);
                results = committeeResults.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject rep = result.getJSONObject(i);
                    committee_names.add(rep.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            TableLayout committees = (TableLayout) findViewById(R.id.committee_list);

            for (int i = 0; i < committee_names.size(); i++) {
                TableRow committee = (TableRow) View.inflate(context, R.layout.bills_committees_row, null);
                TextView committee_name = (TextView) committee.findViewById(R.id.bill_or_committee);
                TableRow row = (TableRow) committee.findViewById(R.id.row_background);
                committee_name.setText(committee_names.get(i));
                committees.addView(committee);
                if (party.equals("D")) {
                    row.setBackgroundResource(R.drawable.outlinethin2);
                    //row.setBackgroundColor(Color.parseColor("#4571CF"));
                } else if (party.equals("R")) {
                    row.setBackgroundResource(R.drawable.outlinethin);
                    //row.setBackgroundColor(Color.parseColor("#E63939"));
                }
            }
        }
    }

    private class getPicture extends AsyncTask<String, String, Bitmap> {

        private Bitmap img;

        @Override
        protected Bitmap doInBackground(String... id) {

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
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            ImageButton picture = (ImageButton) findViewById(R.id.picture);
            picture.setImageBitmap(result);

        }
    }

    private class Bills extends AsyncTask<String, String, JSONArray> {

        private String resp;
        private JSONArray results;

        @Override
        protected JSONArray doInBackground(String... location) {
            String API_URL = "https://congress.api.sunlightfoundation.com/bills/search?sponsor_id=";
            try {
                URL url = new URL(API_URL + id + "&apikey=" + SUNLIGHT_KEY);
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
                JSONObject billResults = new JSONObject(resp);
                results = billResults.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return results;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result)
            int count = 0;
            for (int i = 0; i < results.length(); i++) {
                try {
                    JSONObject bill = result.getJSONObject(i);
                    String bill_title = bill.getString("short_title");
                    String bill_date = bill.getString("last_version_on");
                    String[] tokens = bill_date.split("-");
                    bill_date = tokens[1] + "/" + tokens[2] + "/" + tokens[0];


                    if (bill_title != "null") {
                        bill_names[count] = bill_title;
                        bill_dates[count] = bill_date;
                        count++;
                        if (count == 10) {
                            break;
                        }
                    }
                    Log.d("T", "Bill is " + bill);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            TableLayout bills = (TableLayout) findViewById(R.id.bills_list);

            for (int i = 0; i < 10; i++) {
                TableRow bill = (TableRow) View.inflate(context, R.layout.bills_row, null);
                TextView bill_name = (TextView) bill.findViewById(R.id.bill);
                TextView bill_date = (TextView) bill.findViewById(R.id.bill_date);
                TableRow row = (TableRow) bill.findViewById(R.id.row_background);
                bill_name.setText(bill_names[i]);
                bill_date.setText(bill_dates[i]);
                bills.addView(bill);
                if (party.equals("D")) {
                    row.setBackgroundResource(R.drawable.outlinethin2);
                    //row.setBackgroundColor(Color.parseColor("#4571CF"));
                } else if (party.equals("R")) {
                    row.setBackgroundResource(R.drawable.outlinethin);
                    //row.setBackgroundColor(Color.parseColor("#E63939"));
                }
            }
        }
    }

    private class LegislatorsInfo extends AsyncTask<String, String, JSONObject> {

        private String resp;
        private JSONObject congressman;

        @Override
        protected JSONObject doInBackground(String... id) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()

            String API_URL = "https://congress.api.sunlightfoundation.com/legislators?bioguide_id=";
            try {
                URL url = new URL(API_URL + id[0] + "&apikey=" + SUNLIGHT_KEY);
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
            try {
                JSONObject jObject = new JSONObject(resp);
                JSONArray results = jObject.getJSONArray("results");
                congressman = results.getJSONObject(0);
                Log.d("T", "This is in details " + congressman);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return congressman;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // execution of result of Long time consuming operation
            //finalResult.setText(result);
            try {
                Button name = (Button) findViewById(R.id.name);
                TableRow mainInfo = (TableRow) findViewById(R.id.mainBackground);
                party = result.getString("party");
                if (party.equals("D")) {
                    name.setText(result.getString("first_name") + " " + result.getString("last_name") + " (D)");
                    mainInfo.setBackgroundColor(Color.parseColor("#4571CF"));
                } else if (party.equals("R")) {
                    name.setText(result.getString("first_name") + " " + result.getString("last_name") + " (R)");
                    mainInfo.setBackgroundColor(Color.parseColor("#E63939"));
                }

                end_of_term = result.getString("term_end");
                String[] tokens = end_of_term.split("-");

                TextView term = (TextView) findViewById(R.id.end_of_term);
                term.setText("End of Term: " + tokens[1] + "/" + tokens[2] + "/" + tokens[0]);



                website = result.getString("website");
                email = result.getString("oc_email");
                facebook = result.getString("facebook_id");
                twitter = result.getString("twitter_id");



                new Committees().execute(id);
                new Bills().execute(id);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void twitterClick(View view) {
        Uri uri;
        Intent intent;
        uri = Uri.parse("https://twitter.com/" + twitter);
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void linkClick(View view) {
        Uri uri;
        Intent intent;
        uri = Uri.parse(website);
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void emailClick(View view) {
        Uri uri;
        Intent intent;
        uri = Uri.parse("mailto:" + email);
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void facebookClick(View view) {
        Uri uri;
        Intent intent;
        uri = Uri.parse("https://facebook.com/" + facebook);
        intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
