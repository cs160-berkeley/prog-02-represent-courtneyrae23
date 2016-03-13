package edu.courtneyrae23berkeley.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by court_000 on 3/13/2016.
 */
public class Current_Election_Activity extends Activity {

    String state;
    Context context;
    Boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_election);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        state = b.getString("state");
        context = this;
        new StateVote().execute("dem");
    }

    private class StateVote extends AsyncTask<String, String, JSONObject> {
        String resp;
        JSONObject election_results;

        @Override
        protected JSONObject doInBackground(String... DemOrRep) {
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
                for (int i = 0; i < results.length(); i++) {
                    JSONObject chart = results.getJSONObject(i);
                    if (chart.getString("topic").equals("2016-president-" + DemOrRep[0] + "-primary")) {
                        election_results = chart;
                        break;
                    }
                }
                Log.d("T", "Got state data" + election_results);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return election_results;
        }


        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                TableLayout myLayout = (TableLayout) findViewById(R.id.tableLayout);

                TableRow title = (TableRow) View.inflate(context, R.layout.election_title, null);
                TextView title_view = (TextView) title.findViewById(R.id.title);
                String election_title = result.getString("title");
                Log.d("T", "Title is " + election_title);
                title_view.setText(election_title);
                myLayout.addView(title);

                TableRow date = (TableRow) View.inflate(context, R.layout.election_title, null);
                TextView date_view = (TextView) date.findViewById(R.id.title);
                String election_date = result.getString("election_date");
                String[] tokens = election_date.split("-");
                election_date = tokens[1] + "/" + tokens[2] + "/" + tokens[0];
                Log.d("T", "Title is " + election_date);
                date_view.setText(election_date);
                myLayout.addView(date);

                JSONArray estimates = result.getJSONArray("estimates");
                for (int i = 0; i < estimates.length(); i++) {
                    TableRow candidate = (TableRow) View.inflate(context, R.layout.current_election_row, null);
                    JSONObject obj = estimates.getJSONObject(i);

                    TextView value = (TextView) candidate.findViewById(R.id.value);
                    value.setText(obj.getString("value") + "%");

                    ImageButton picture = (ImageButton) findViewById(R.id.picture);

                    TextView name = (TextView) candidate.findViewById(R.id.name);
                    TableRow row = (TableRow) candidate.findViewById(R.id.row_background);
                    if (obj.getString("party").equals("Rep")) {
                        row.setBackgroundResource(R.drawable.outlinethin);
                        name.setText(obj.getString("first_name") + " " + obj.getString("last_name") + " (R)");
                    } else if (obj.getString("party").equals("Dem")) {
                        row.setBackgroundResource(R.drawable.outlinethin2);
                        name.setText(obj.getString("first_name") + " " + obj.getString("last_name") + " (D)");
                    } else {
                        name.setText(obj.getString("choice"));
                        row.setBackgroundResource(R.drawable.outline3);
                    }
                    myLayout.addView(candidate);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (first) {
                new StateVote().execute("gop");
                first = false;
            }
        }
    }
}
