package com.rnp.zaqzilla.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rnp.zaqzilla.R;
import com.rnp.zaqzilla.StatusViewActivity;
import com.rnp.zaqzilla.adapter.TwitterListAdapter;
import com.rnp.zaqzilla.helper.InternalStorage;

public class TimelineFragments extends Fragment {

    private ListView               listView;
    private List<twitter4j.Status> status10;
    private Drawable               img;

    private final String           TIMELINE_IMAGE = "3";
    private final String           TIMELINE_FEED  = "4";

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timeline, container, false);
        listView = (ListView) v.findViewById(R.id.listView1);
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

        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        try {
            List<Status> statuses = twitter.getUserTimeline(user);

            return statuses;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
