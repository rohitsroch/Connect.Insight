package com.rohitdeveloper.connectinsight;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class TwitterLoginActivity extends AppCompatActivity {
    private static final String TAG = "TwitterLoginActivity";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY ="szOipZZODEQ1EYlQ2mKoV9d5x";
    private static final String TWITTER_SECRET ="O9wJdjkJMY4W1oE7EbU4T7aRQYvPkdJIEeDQZW2u774bksq7hn";


    private EditText twitterHashtag;
    private TextView twitterLoginDescription;

    private TwitterLoginButton loginButton;

    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_twitter_login);

        twitterHashtag=(EditText) findViewById(R.id.id_twitter_hash_tag);
        twitterLoginDescription=(TextView) findViewById(R.id.id_twitter_login_description);

        String description = "Please "+"<font color='#cc0000'>connect to Twitter</font>"+" so that we can learn about you";
        twitterLoginDescription.setText(fromHtml(description));

        latitude = getIntent().getDoubleExtra("Latitude",0);
        longitude =getIntent().getDoubleExtra("Longitude",0);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Log.d(TAG,msg);
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                String query=twitterHashtag.getText().toString();
                if(!query.isEmpty()) {
                    Intent connectionActivityIntent = new Intent(TwitterLoginActivity.this, ConnectionActivity.class);
                    connectionActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    connectionActivityIntent.putExtra("UserName", session.getUserName());
                    connectionActivityIntent.putExtra("UserId", session.getUserId());
                    connectionActivityIntent.putExtra("Query", twitterHashtag.getText().toString());
                    connectionActivityIntent.putExtra("Latitude", latitude);
                    connectionActivityIntent.putExtra("Longitude", longitude);
                    startActivity(connectionActivityIntent);
                }else{
                    Toast.makeText(TwitterLoginActivity.this,"Query cannot be empty",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }



    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
