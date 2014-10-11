package com.rnp.zaqzilla;

import org.ocpsoft.prettytime.PrettyTime;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import twitter4j.Status;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusViewActivity extends Activity {

    private ImageView imageView;
    private TextView  textUser;
    private TextView  textStatus;
    private TextView  textCreatedAt;
    private TextView  textScrenName;

    private Status    status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_view);
        imageView = (ImageView) findViewById(R.id.imageProfile);
        textUser = (TextView) findViewById(R.id.username);
        textStatus = (TextView) findViewById(R.id.status);
        textCreatedAt = (TextView) findViewById(R.id.createdAt);
        textScrenName = (TextView) findViewById(R.id.screenname);
        try {
            status = (Status) getIntent().getSerializableExtra("status");
            PrettyTime p = new PrettyTime();
            if (status != null) {
                textUser.setText(status.getUser().getName());
                textScrenName.setText("@" + status.getUser().getScreenName());
                textStatus.setText(status.getText());
                textCreatedAt.setText(p.format(status.getCreatedAt()));
                UrlImageViewHelper.setUrlDrawable(imageView, status.getUser()
                        .getBiggerProfileImageURL());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status_view, menu);
        return true;
    }

}
