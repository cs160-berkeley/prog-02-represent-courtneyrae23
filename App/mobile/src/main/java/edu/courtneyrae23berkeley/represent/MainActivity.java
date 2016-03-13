package edu.courtneyrae23berkeley.represent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "oyVupTpHO3kVQYK6dahhgwEJn";
    private static final String TWITTER_SECRET = "bNMKaQhZjjJP1DsYJYXSPsaU4Wn1SPzP3ctB1BbxhaiUEAd803";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);


        final EditText tx = (EditText)findViewById(R.id.zipCode);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Kozuka.otf");
        tx.setTypeface(custom_font);

        tx.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (tx.getText().length() < 5) {
                        Toast.makeText(MainActivity.this, "Please Enter a 5-digit ZIP Code", Toast.LENGTH_SHORT).show();
                    } else {
                        //if (tx.getText()) not in master list of zipcodes
                        // send an error message
                        Bundle b = new Bundle();
                        b.putBoolean("zipcode", true);
                        b.putString("ZIP", tx.getText().toString());
                        b.putString("MessageType", "start");
                        Intent intent = new Intent(MainActivity.this, Congressional_Activity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });

        TextView oruse = (TextView)findViewById(R.id.textView);
        oruse.setTypeface(custom_font);

        Button buttontx = (Button)findViewById(R.id.CurrLocButton);
        buttontx.setTypeface(custom_font);

        TextView title = (TextView) findViewById(R.id.mainTitle);
        title.setTypeface(custom_font);

    }

    public void sendMessage(View view)
    {
        Bundle b = new Bundle();
        b.putBoolean("zipcode", false);
        b.putString("ZIP", "");
        b.putString("MessageType", "start");
        Intent intent = new Intent(MainActivity.this, Congressional_Activity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
