package com.rnp.zaqzilla.adapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;

import twitter4j.Status;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.rnp.zaqzilla.R;
import com.rnp.zaqzilla.helper.InternalStorage;

public class TwitterListAdapter extends ArrayAdapter<Status> {
    private Activity     activity;
    private int          layaoutResourceId;
    List<Status>         listStatus;
    PrettyTime           p;
    private final String TIMELINE_IMAGE = "3";

    private Drawable     imgFromCache;

    public TwitterListAdapter(Activity activity, int resource,
            List<Status> objects) {
        super(activity, resource, objects);
        this.activity = activity;
        this.layaoutResourceId = resource;
        this.listStatus = objects;
        p = new PrettyTime();
    }

    public TwitterListAdapter(Activity activity, int resource,
            List<Status> objects, Drawable fromCache) {
        super(activity, resource, objects);
        this.activity = activity;
        this.layaoutResourceId = resource;
        this.listStatus = objects;
        p = new PrettyTime();
        this.imgFromCache = fromCache;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder = new Holder();
        Status status = listStatus.get(position);
        // System.out.println(status.toString());

        convertView = activity.getLayoutInflater().inflate(layaoutResourceId,
                parent, false);
        holder.profilePict = (ImageView) convertView
                .findViewById(R.id.imageProfile);
        holder.textViewUser = (TextView) convertView
                .findViewById(R.id.username);
        holder.textViewStatus = (TextView) convertView
                .findViewById(R.id.status);
        holder.textViewCreatedAt = (TextView) convertView
                .findViewById(R.id.createdAt);
        convertView.setTag(holder);

        holder.textViewUser.setText(status.getUser().getName());
        holder.textViewStatus.setText(status.getText());
        holder.textViewCreatedAt.setText(p.format(status.getCreatedAt()));

        // for first image, cache it
        if (imgFromCache == null) {

            UrlImageViewHelper.setUrlDrawable(holder.profilePict, status
                    .getUser().getBiggerProfileImageURL(),
                    new UrlImageViewCallback() {

                        @Override
                        public void onLoaded(ImageView arg0, Bitmap arg1,
                                String arg2, boolean arg3) {
                            try {

                                InternalStorage.writeBitmap(activity,
                                        TIMELINE_IMAGE, arg1);
                                holder.profilePict.setImageBitmap(arg1);
                            } catch (FileNotFoundException e) {

                                e.printStackTrace();
                            } catch (IOException e) {

                                e.printStackTrace();
                            }

                        }
                    });

        } else {
            holder.profilePict.setImageDrawable(imgFromCache);
        }
        return convertView;
    }

    static class Holder {
        ImageView profilePict;
        TextView  textViewUser;
        TextView  textViewStatus;
        TextView  textViewCreatedAt;

    }

}
