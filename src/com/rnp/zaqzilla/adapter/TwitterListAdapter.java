package com.rnp.zaqzilla.adapter;

import java.util.List;

import com.rnp.zaqzilla.R;

import twitter4j.Status;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TwitterListAdapter extends ArrayAdapter<Status> {
    private Activity activity;
    private int      layaoutResourceId;
    List<Status>     listStatus;

    public TwitterListAdapter(Activity activity, int resource,
            List<Status> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.layaoutResourceId = resource;
        this.listStatus = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        Status status = listStatus.get(position);
       // System.out.println(status.toString());
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(
                    layaoutResourceId, parent, false);
            holder.profilePict = (ImageView) convertView
                    .findViewById(R.id.imageProfile);
            holder.textViewUser = (TextView) convertView
                    .findViewById(R.id.username);
            holder.textViewStatus = (TextView) convertView
                    .findViewById(R.id.status);
            holder.textViewCreatedAt = (TextView) convertView
                    .findViewById(R.id.createdAt);
            convertView.setTag(holder);

        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.textViewUser.setText(status.getUser().getName());
        holder.textViewStatus.setText(status.getText());
        holder.textViewCreatedAt.setText(status.getCreatedAt().toString());

        return convertView;
    }

    static class Holder {
        ImageView profilePict;
        TextView  textViewUser;
        TextView  textViewStatus;
        TextView  textViewCreatedAt;

    }

}
