package edu.courtneyrae23berkeley.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by court_000 on 2/24/2016.
 */
public class Congressional_Activity extends Activity {

    //Will do work to find the details of the representatives from the web
    int totalReps;
    ArrayList<String> repNames = new ArrayList<String>();
    ArrayList<String> parties = new ArrayList<String>();
    boolean isZipCode;
    String zip;
    String toastText;
    //hardcoded website links
    String[] links = new String[4];
    String[] elinks = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congressional);

        RelativeLayout back_dim_layout = (RelativeLayout) findViewById(R.id.bac_dim_layout);

        Intent in = getIntent();
        Bundle b = in.getExtras();

        String message = b.getString("MessageType");
        Log.d("T", "got the message.....");

        if (message.equals("shake")) {
            Log.d("T", "got the message! Shake!");
            isZipCode = false;
            zip = "";

            DecimalFormat df = new DecimalFormat("#.####");

            double minLat = -90.00;
            double maxLat = 90.00;
            double latitude = Math.floor((minLat + Math.random() * ((maxLat - minLat) + 1)) * 10000) / 10000;
            double minLon = 0.00;
            double maxLon = 180.00;
            double longitude = Math.floor((minLon + Math.random() * ((maxLon - minLon) + 1)) * 10000) / 10000;

            toastText = "New location: " + latitude + ", " + longitude;

        } else if (message.equals("start")) {
            Log.d("T", "got the message! Start!");
            isZipCode = b.getBoolean("zipcode");
            zip = b.getString("ZIP");
        }

        //get RepNames & parties for all representatives in this location --dummy data
        totalReps = 4;

        repNames.add("Dean Heller");
        repNames.add("Harry Reid");
        repNames.add("Joseph Heck");
        repNames.add("Cresent Hardy");

        parties.add("Republican");
        parties.add("Democrat");
        parties.add("Republican");
        parties.add("Republican");

        links[0] = "http://www.heller.senate.gov/public/";
        links[1] = "http://www.reid.senate.gov/";
        links[2] = "https://heck.house.gov/";
        links[3] = "https://hardy.house.gov/";

        elinks[0] = "deanheller@senate.com";
        elinks[1] = "harryreid@senate.com";
        elinks[2] = "josephheck@rep.com";
        elinks[3] = "cresenthardy@rep.com";

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);

        TableRow senTitle = (TableRow) View.inflate(this, R.layout.title_row, null);
        TextView senText = (TextView) senTitle.findViewById(R.id.senRepTitle);
        senText.setText("Senators");
        tableLayout.addView(senTitle);

        for (int i = 0; i < 2; i++) {
            TableRow senator = (TableRow) View.inflate(this, R.layout.congress_row, null);

            Button senName = (Button) senator.findViewById(R.id.name);
            senName.setTag(i);

            TableRow background = (TableRow) senator.findViewById(R.id.row_background);

            ImageButton picture = (ImageButton) senator.findViewById(R.id.picture);
            picture.setTag(i);

            Button website = (Button) senator.findViewById(R.id.website);
            website.setTag(i);
            
            Button moreInfo = (Button) senator.findViewById(R.id.moreinfo);
            moreInfo.setTag(i);

            Button email = (Button) senator.findViewById(R.id.email);
            email.setTag(i);

            if (i == 0) {
                picture.setBackgroundResource(R.drawable.deanheller);
            } else if (i == 1) {
                picture.setBackgroundResource(R.drawable.harryreid);
            }

            if (parties.get(i) == "Republican") {
                senName.setText(repNames.get(i) + " (R)");
                background.setBackgroundColor(Color.parseColor("#E63939"));
            } else if (parties.get(i) == "Democrat") {
                senName.setText(repNames.get(i) + " (D)");
                background.setBackgroundColor(Color.parseColor("#4571CF"));
            }
            tableLayout.addView(senator);
        }

        TableRow repTitle = (TableRow) View.inflate(this, R.layout.title_row, null);
        TextView repText = (TextView) repTitle.findViewById(R.id.senRepTitle);
        repText.setText("Representatives");
        tableLayout.addView(repTitle);

        TextView zipCodeMode = (TextView) findViewById(R.id.zipCodeTitle);
        TextView stateLoc = (TextView) findViewById(R.id.stateTitle);


        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);

        if (isZipCode) {
            zipCodeMode.setText("ZIP Code: " + zip);
            stateLoc.setText("Nevada");
            sendIntent.putExtra("Location", zip);
        } else {
            zipCodeMode.setText("Current Location");
            stateLoc.setText("Ruth, Nevada");
            sendIntent.putExtra("Location", "Ruth, Nevada");
        }

        for (int i = 2; i < totalReps; i++) {
            TableRow rep = (TableRow) View.inflate(this, R.layout.congress_row, null);

            Button repName = (Button) rep.findViewById(R.id.name);
            repName.setTag(i);

            TableRow background = (TableRow) rep.findViewById(R.id.row_background);

            ImageButton picture = (ImageButton) rep.findViewById(R.id.picture);
            picture.setTag(i);

            Button moreInfo = (Button) rep.findViewById(R.id.moreinfo);
            moreInfo.setTag(i);

            Button email = (Button) rep.findViewById(R.id.email);
            email.setTag(i);

            if (i == 2) {
                picture.setBackgroundResource(R.drawable.joeheck);
            } else if (i == 3) {
                picture.setBackgroundResource(R.drawable.cresenthardy);
            }

            if (parties.get(i) == "Republican") {
                repName.setText(repNames.get(i) + " (R)");
                background.setBackgroundColor(Color.parseColor("#E63939"));
            } else if (parties.get(i) == "Democrat") {
                repName.setText(repNames.get(i) + " (D)");
                background.setBackgroundColor(Color.parseColor("#4571CF"));
            }
            tableLayout.addView(rep);
        }

        if (message.equals("shake")) {
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
        }

        sendIntent.putExtra("repNames", repNames);
        sendIntent.putExtra("parties", parties);
        startService(sendIntent);
    }


    public void initiatePopUpWindow(View view) {
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.pop_up_tweet, null);
        final PopupWindow mPopupWindow = new PopupWindow(popupView,1000, 470, true);

        // Closes the popup window when touch outside of it - when looses focus
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);

        // Removes default black background
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


    }

    public void detailedclick(View view)
    {
        Intent intent = new Intent(Congressional_Activity.this, Detailed_Activity.class);
        switch((Integer) view.getTag())
        {
            // 7 cases for the max possibility of 2 senators + 5 reps for one zipcode
            case 0:
                intent.putExtra("CurrentRep", 0);
                intent.putExtra("repName", repNames.get(0));
                intent.putExtra("party", parties.get(0));
                startActivity(intent);
                break;
            case 1:
                intent.putExtra("CurrentRep", 1);
                intent.putExtra("repName", repNames.get(1));
                intent.putExtra("party", parties.get(1));
                startActivity(intent);
                break;
            case 2:
                intent.putExtra("CurrentRep", 2);
                intent.putExtra("repName", repNames.get(2));
                intent.putExtra("party", parties.get(2));
                startActivity(intent);
                break;
            case 3:
                intent.putExtra("CurrentRep", 3);
                intent.putExtra("repName", repNames.get(3));
                intent.putExtra("party", parties.get(3));
                startActivity(intent);
                break;
            case 4:
                intent.putExtra("CurrentRep", 4);
                intent.putExtra("repName", repNames.get(4));
                intent.putExtra("party", parties.get(4));
                startActivity(intent);
                break;
            case 5:
                intent.putExtra("CurrentRep", 5);
                intent.putExtra("repName", repNames.get(5));
                intent.putExtra("party", parties.get(5));
                startActivity(intent);
                break;
            case 6:
                intent.putExtra("CurrentRep", 6);
                intent.putExtra("repName", repNames.get(6));
                intent.putExtra("party", parties.get(6));
                startActivity(intent);
                break;
            case 7:
                intent.putExtra("CurrentRep", 7);
                intent.putExtra("repName", repNames.get(7));
                intent.putExtra("party", parties.get(7));
                startActivity(intent);
                break;
        }
    }

    public void linkClick(View view)
    {
        Uri uri;
        Intent intent;
        switch((Integer) view.getTag())
        {
            // 7 cases for the max possibility of 2 senators + 5 reps for one zipcode
            case 0:
                uri = Uri.parse(links[0]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 1:
                uri = Uri.parse(links[1]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 2:
                uri = Uri.parse(links[2]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 3:
                uri = Uri.parse(links[3]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 4:
                uri = Uri.parse(links[4]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 5:
                uri = Uri.parse(links[5]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 6:
                uri = Uri.parse(links[6]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 7:
                uri = Uri.parse(links[7]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    public void emailClick(View view)
    {
        Uri uri;
        Intent intent;
        switch((Integer) view.getTag())
        {
            // 7 cases for the max possibility of 2 senators + 5 reps for one zipcode
            case 0:
                uri = Uri.parse("mailto:" + elinks[0]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 1:
                uri = Uri.parse("mailto:" +elinks[1]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 2:
                uri = Uri.parse("mailto:" +elinks[2]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 3:
                uri = Uri.parse("mailto:" +elinks[3]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 4:
                uri = Uri.parse("mailto:" +elinks[4]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 5:
                uri = Uri.parse("mailto:" +elinks[5]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 6:
                uri = Uri.parse("mailto:" +elinks[6]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case 7:
                uri = Uri.parse("mailto:" +elinks[7]);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }
}
