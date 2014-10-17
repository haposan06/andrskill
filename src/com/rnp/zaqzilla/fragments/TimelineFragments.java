package com.rnp.zaqzilla.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rnp.zaqzilla.R;
import com.rnp.zaqzilla.StatusViewActivity;
import com.rnp.zaqzilla.TwitterLogin;
import com.rnp.zaqzilla.adapter.TwitterListAdapter;
import com.rnp.zaqzilla.helper.InternalStorage;

public class TimelineFragments extends Fragment {

    private ListView                 listView;
    private List<twitter4j.Status>   status10;
    private Drawable                 img;

    private final String             TIMELINE_IMAGE = "3";
    private final String             TIMELINE_FEED  = "4";

    // Constants

    private boolean                  isTwitterLogin = false;

    private SharedPreferences        sp;
    private SharedPreferences.Editor spe;
    private Button                   btnLogin;
    private Button                   btnLogout;
    private Twitter                  twitter;
    private static RequestToken      requestToken;
    private AccessToken              accessToken;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timeline, container, false);
        sp = getActivity().getSharedPreferences(Const.PREFERENCE_NAME, 0);
        spe = sp.edit();

        isTwitterLogin = sp.getBoolean("login", false);
        listView = (ListView) v.findViewById(R.id.listView1);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnLogout = (Button) v.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                spe.remove(Const.PREF_KEY_TOKEN);
                spe.remove(Const.PREF_KEY_SECRET);
                spe.remove("login");

                spe.commit();
                listView.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.GONE);

            }
        });
        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isConnected()) {

                } else {
                    /* askOAuth(); */
                    Intent itx = new Intent(getActivity(), TwitterLogin.class);
                    getActivity().startActivity(itx);
                }

            }
        });

        if (isTwitterLogin) {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            try {
                status10 = (List<twitter4j.Status>) InternalStorage.readObject(
                        getActivity(), TIMELINE_FEED);
                img = InternalStorage.readImage(getActivity(), TIMELINE_IMAGE);
                TwitterListAdapter adapter = new TwitterListAdapter(
                        getActivity(), R.layout.twitter_list_layout, status10,
                        img);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } catch (IOException e) {

                e.printStackTrace();
            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }
            new TimelineAsync().execute();
        } else {
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        return v;
    }

    private class TimelineAsync extends AsyncTask<Void, Void, List<Status>> {

        @Override
        protected List<twitter4j.Status> doInBackground(Void... params) {

            return getTimeline();
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> status) {
            // cache data

            // put into list
            status10 = new ArrayList<twitter4j.Status>();
            for (int i = 0; i < 10; i++) {
                status10.add(status.get(i));
            }
            try {
                InternalStorage.writeObject(getActivity(), TIMELINE_FEED,
                        status);
            } catch (IOException e) {

                e.printStackTrace();
            }
            TwitterListAdapter adapter = new TwitterListAdapter(getActivity(),
                    R.layout.twitter_list_layout, status10);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    twitter4j.Status status = status10.get(arg2);
                    Intent intent = new Intent(getActivity(),
                            StatusViewActivity.class);
                    intent.putExtra("status", status);
                    startActivity(intent);
                }
            });

        }

    }

    private List<Status> getTimeline() {
        String user = "7langit";
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setDebugEnabled(true)
                .setOAuthConsumerKey(Const.CONSUMER_KEY)
                .setOAuthConsumerSecret(Const.CONSUMER_SECRET)
                .setOAuthAccessToken(sp.getString(Const.PREF_KEY_TOKEN, ""))
                .setOAuthAccessTokenSecret(
                        sp.getString(Const.PREF_KEY_SECRET, ""))
                .setUseSSL(true)

        ;

        twitter = new TwitterFactory(builder.build()).getInstance();
        try {
            List<Status> statuses = twitter.getUserTimeline(user);

            return statuses;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /*
     * private void loginToTwitter() {
     * // Check if already logged in
     * if (!isTwitterLogin) {
     * ConfigurationBuilder builder = new ConfigurationBuilder();
     * builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
     * builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
     * builder.setUseSSL(true);
     * Configuration configuration = builder.build();
     * TwitterFactory factory = new TwitterFactory(configuration);
     * twitter = factory.getInstance();
     * Thread thread = new Thread(new Runnable() {
     * @Override
     * public void run() {
     * try {
     * requestToken = twitter
     * .getOAuthRequestToken(TWITTER_CALLBACK_URL);
     * String url= requestToken.getAuthenticationURL();
     * url= url.replace("http", "https");
     * getActivity().startActivity(
     * new Intent(Intent.ACTION_VIEW, Uri
     * .parse(requestToken
     * .getAuthenticationURL())));
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * });
     * thread.start();
     * } else {
     * // user already logged into twitter
     * Toast.makeText(getActivity(), "Already Logged into twitter",
     * Toast.LENGTH_LONG).show();
     * new TimelineAsync().execute();
     * }
     * }
     */
    // private void askOAuth() {
    // ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    // configurationBuilder.setOAuthConsumerKey(Const.CONSUMER_KEY);
    // configurationBuilder.setOAuthConsumerSecret(Const.CONSUMER_SECRET);
    // configurationBuilder.setUseSSL(true);
    // Configuration configuration = configurationBuilder.build();
    //
    // twitter = new TwitterFactory(configuration).getInstance();
    //
    // try {
    // Thread thread = new Thread(new Runnable() {
    // public void run() {
    // try {
    // requestToken = twitter
    // .getOAuthRequestToken(Const.CALLBACK_URL);
    // Toast.makeText(getActivity(),
    // "Please authorize this app!", Toast.LENGTH_LONG)
    // .show();
    // String url = requestToken.getAuthenticationURL();
    // url = url.replace("http", "https");
    // TimelineFragments.this.startActivity(new Intent(
    // Intent.ACTION_VIEW, Uri.parse(url)));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // });
    // thread.start();
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    private boolean isConnected() {
        return sp.getString(Const.PREF_KEY_TOKEN, null) != null;
    }

    public static class Const {
        public static String       CONSUMER_KEY          = "0kQImuk3lHPo5TbM0PAfOs8gx";
        public static String       CONSUMER_SECRET       = "x9gwWw3uyGQgu4pMFexkGW1j1SS6xEYc4MdQoZ3GQuZpGYd10u";

        public static String       PREFERENCE_NAME       = "twitter_oauth";
        public static final String PREF_KEY_SECRET       = "oauth_token_secret";
        public static final String PREF_KEY_TOKEN        = "oauth_token";

        public static final String CALLBACK_URL          = "oauth://t4jsample";

        public static final String IEXTRA_AUTH_URL       = "auth_url";
        public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
        public static final String IEXTRA_OAUTH_TOKEN    = "oauth_token";
    }

}
