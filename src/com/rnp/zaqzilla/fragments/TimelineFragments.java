package com.rnp.zaqzilla.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.rnp.zaqzilla.MainActivity;
import com.rnp.zaqzilla.R;
import com.rnp.zaqzilla.StatusViewActivity;
import com.rnp.zaqzilla.adapter.TwitterListAdapter;
import com.rnp.zaqzilla.helper.InternalStorage;

public class TimelineFragments extends Fragment {

    private ListView                 listView;
    private List<twitter4j.Status>   status10;
    private Drawable                 img;

    private final String             TIMELINE_IMAGE             = "3";
    private final String             TIMELINE_FEED              = "4";

    // Constants

    static String                    TWITTER_CONSUMER_KEY       = "0kQImuk3lHPo5TbM0PAfOs8gx";                         // place
                                                                                                                        // your
                                                                                                                        // cosumer
                                                                                                                        // key
                                                                                                                        // here
    static String                    TWITTER_CONSUMER_SECRET    = "x9gwWw3uyGQgu4pMFexkGW1j1SS6xEYc4MdQoZ3GQuZpGYd10u"; // place
                                                                                                                        // your
                                                                                                                        // consumer
                                                                                                                        // secret
                                                                                                                        // here

    // Preference Constants
    static String                    PREFERENCE_NAME            = "twitter_oauth";
    static final String              PREF_KEY_OAUTH_TOKEN       = "oauth_token";
    static final String              PREF_KEY_OAUTH_SECRET      = "oauth_token_secret";
    static final String              PREF_KEY_TWITTER_LOGIN     = "isTwitterLogedIn";

    static final String              TWITTER_CALLBACK_URL       = "oauth://t4jsample";

    // Twitter oauth urls
    static final String              URL_TWITTER_AUTH           = "auth_url";
    static final String              URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    static final String              URL_TWITTER_OAUTH_TOKEN    = "oauth_token";

    private boolean                  isTwitterLogin             = false;

    private SharedPreferences        sp;
    private SharedPreferences.Editor spe;
    private Button                   btnLogin;
    private Button                   btnLogout;
    private Twitter                  twitter;
    private static RequestToken requestToken;
    private AccessToken accessToken;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timeline, container, false);
        sp = getActivity().getSharedPreferences("zaqzilla", 0);
        spe = sp.edit();

        isTwitterLogin = sp.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
        listView = (ListView) v.findViewById(R.id.listView1);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnLogout = (Button) v.findViewById(R.id.btnLogout);

        if (isTwitterLogin) {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
        // get from cache first
        try {
            status10 = (List<twitter4j.Status>) InternalStorage.readObject(
                    getActivity(), TIMELINE_FEED);
            img = InternalStorage.readImage(getActivity(), TIMELINE_IMAGE);
            TwitterListAdapter adapter = new TwitterListAdapter(getActivity(),
                    R.layout.twitter_list_layout, status10, img);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (IOException e) {

            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }

        new TimelineAsync().execute();
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
                .setOAuthConsumerKey("0kQImuk3lHPo5TbM0PAfOs8gx")
                .setOAuthConsumerSecret(
                        "x9gwWw3uyGQgu4pMFexkGW1j1SS6xEYc4MdQoZ3GQuZpGYd10u")
                .setOAuthAccessToken(
                        "55638769-Mj4VZZ1xXMWSzcpCSM5AXLvoDAF7eaW5SJ0XFXGgM")
                .setOAuthAccessTokenSecret(
                        "7N3nP5IUWmZxU349XwncHPTXFEIeWIxAoNd5RJqFOgNnM");

        twitter = new TwitterFactory(builder.build()).getInstance();
        try {
            List<Status> statuses = twitter.getUserTimeline(user);

            return statuses;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void loginToTwitter() {
        // Check if already logged in
        if (isTwitterLogin) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        requestToken = twitter
                                .getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        getActivity().startActivity(new Intent(
                                Intent.ACTION_VIEW, Uri.parse(requestToken
                                        .getAuthenticationURL())));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            // user already logged into twitter
            Toast.makeText(getActivity(),
                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }

}
