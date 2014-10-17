package com.rnp.zaqzilla;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rnp.zaqzilla.fragments.TimelineFragments.Const;

public class TwitterLogin extends Activity {
    private static final String      TAG     = "T4JSample";

    private Button                   buttonLogin;
    private Button                   getTweetButton;
    private TextView                 tweetText;
    private ScrollView               scrollView;

    private static Twitter           twitter;
    private static RequestToken      requestToken;
    private static SharedPreferences mSharedPreferences;
    private boolean                  running = false;
    private String                   verifier;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.main);

        mSharedPreferences = getSharedPreferences(Const.PREFERENCE_NAME,
                MODE_PRIVATE);

        /**
         * Handle OAuth Callback
         */
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(Const.CALLBACK_URL)) {
            verifier = uri.getQueryParameter(Const.IEXTRA_OAUTH_VERIFIER);
            try {
                new AccessTokenAsync().execute();
            } catch (Exception e) {
                // Log.e(TAG, e.getMessage());
                // Toast.makeText(this, e.getMessage(),
                // Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        new OuathAsync().execute();

    }

    private class AccessTokenAsync extends AsyncTask<Void, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(Void... params) {
            AccessToken accessToken = null;
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken,
                        verifier);
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            if (accessToken != null) {
                Editor e = mSharedPreferences.edit();
                e.putString(Const.PREF_KEY_TOKEN, accessToken.getToken());
                e.putString(Const.PREF_KEY_SECRET, accessToken.getTokenSecret());
                e.putBoolean("login", true);
                e.commit();
                Intent intent = new Intent(TwitterLogin.this,
                        MenuActivity.class);
                intent.putExtra("isFromTwitterLogin", true);
                startActivity(intent);
            } else {
                Toast.makeText(TwitterLogin.this, "failedto login",
                        Toast.LENGTH_LONG).show();

            }
        }

    }

    /**
     * check if the account is authorized
     * 
     * @return
     */
    private boolean isConnected() {
        return mSharedPreferences.getString(Const.PREF_KEY_TOKEN, null) != null;
    }

    private class OuathAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            askOAuth();
            return null;
        }

    }

    private void askOAuth() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
        configurationBuilder.setUseSSL(true);
        Configuration configuration = configurationBuilder.build();

        twitter = new TwitterFactory(configuration).getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(Const.CALLBACK_URL);
            /*
             * Toast.makeText(this, "Please authorize this app!",
             * Toast.LENGTH_LONG).show();
             */
            String url = requestToken.getAuthenticationURL();
            url = url.replace("http", "https");
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (TwitterException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }
    }

    /**
     * Remove Token, Secret from preferences
     */
    private void disconnectTwitter() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(Const.PREF_KEY_TOKEN);
        editor.remove(Const.PREF_KEY_SECRET);

        editor.commit();
    }

}
