package edu.courtneyrae23berkeley.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by court_000 on 2/28/2016.
 */
public class Detailed_Activity extends Activity {

    String[] bill_names = new String[5]; // Top 5 most recent bills
    ArrayList<String> committee_names = new ArrayList<String>();
    String repName;
    String party;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
           repName = b.getString("repName");
           party = b.getString("party");
        }


        TextView name = (TextView) findViewById(R.id.name);
        TableRow mainInfo = (TableRow) findViewById(R.id.mainBackground);
        if (party.equals("Democrat")) {
            name.setText(repName + " (D)");
            mainInfo.setBackgroundColor(Color.parseColor("#4571CF"));
        } else if (party.equals("Republican")) {
            name.setText(repName + " (R)");
            mainInfo.setBackgroundColor(Color.parseColor("#E63939"));
        }


        //get names of the committees this rep works on
        String[] dummy_committees = new String[1];
        dummy_committees[0] = "Senate Select Committee On Intelligence";

        for (int i = 0; i < dummy_committees.length; i++) {
            committee_names.add(dummy_committees[i]);
        }

        //get the names of the 5 most recently sponsored bills
        bill_names[0] = "Fair Day in Court for Kids Act of 2016";
        bill_names[1] = "Defeat ISIS and Protect and Secure the United States Act of 2015";
        bill_names[2] = "Moapa Band of Paiutes Land Conveyance Act";
        bill_names[3] = "Nuclear Waste Informed Consent Act";
        bill_names[4] = "Nevada Native Nations Land Act";

        TableLayout committees = (TableLayout) findViewById(R.id.committee_list);
        TableLayout bills = (TableLayout) findViewById(R.id.bills_list);

        for (int i = 0; i < 5; i++) {
            TableRow bill = (TableRow) View.inflate(this, R.layout.bills_committees_row, null);
            TextView bill_name = (TextView) bill.findViewById(R.id.bill_or_committee);
            TableRow row = (TableRow) bill.findViewById(R.id.row_background);
            bill_name.setText(bill_names[i]);
            bills.addView(bill);
            if (party.equals("Democrat")) {
                row.setBackgroundColor(Color.parseColor("#4571CF"));
            } else if (party.equals("Republican")) {
               row.setBackgroundColor(Color.parseColor("#E63939"));
            }
        }

        for (int i = 0; i < dummy_committees.length; i++) {
            TableRow committee = (TableRow) View.inflate(this, R.layout.bills_committees_row, null);
            TextView committee_name = (TextView) committee.findViewById(R.id.bill_or_committee);
            TableRow row = (TableRow) committee.findViewById(R.id.row_background);
            committee_name.setText(committee_names.get(i));
            committees.addView(committee);
            if (party.equals("Democrat")) {
                row.setBackgroundColor(Color.parseColor("#4571CF"));
            } else if (party.equals("Republican")) {
                row.setBackgroundColor(Color.parseColor("#E63939"));
            }
        }

    }
}
