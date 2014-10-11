package com.rnp.zaqzilla.fragments;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rnp.zaqzilla.R;
import com.rnp.zaqzilla.adapter.TwitterListAdapter;

public class TimelineFragments extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timeline, container, false);
        listView = (ListView) v.findViewById(R.id.listView1);
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
            TwitterListAdapter adapter = new TwitterListAdapter(getActivity(),
                    R.layout.twitter_list_layout, status);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    private List<Status> getTimeline() {
        String user = "chelseafc";
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
            List<Status> status10 = new ArrayList<Status>();
            for (int i = 0; i < 10; i++) {
                status10.add(statuses.get(i));
            }
            return status10;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
