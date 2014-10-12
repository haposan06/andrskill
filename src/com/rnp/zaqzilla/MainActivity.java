package com.rnp.zaqzilla;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.Window;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class MainActivity extends FragmentActivity {

    private boolean           isResumed = false;
    private UiLifecycleHelper helper;

    private LoginButton       button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_main);
        helper = new UiLifecycleHelper(this, callback);
        helper.onCreate(savedInstanceState);
        button = (LoginButton) findViewById(R.id.login_button);
        button.setReadPermissions(Arrays.asList("public_profile",
                "user_location", "user_birthday"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        helper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        helper.onPause();
        isResumed = false;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {

            Intent intent = new Intent(this, MenuActivity.class);
            this.startActivity(intent);
        } else {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        helper.onSaveInstanceState(state);
    }

    private void onSessionStateChanged(Session session, SessionState state,
            Exception exception) {
        if (isResumed) {
            FragmentManager fm = getSupportFragmentManager();
            int backStackSize = fm.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                fm.popBackStack();
            }

            if (state.isOpened()) {
                /* showFragment(SELECTION, false); */
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);

            } else if (state.isClosed()) {

            }
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {

                                                @Override
                                                public void call(
                                                        Session session,
                                                        SessionState state,
                                                        Exception exception) {

                                                    onSessionStateChanged(
                                                            session, state,
                                                            exception);
                                                }
                                            };

}
