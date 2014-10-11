package com.rnp.zaqzilla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class MainActivity extends FragmentActivity {

    private static final int  SPLASH         = 0;
    private static final int  SELECTION      = 1;
    private static final int  FRAGMENT_COUNT = 2;
    private Fragment[]        fragments      = new Fragment[FRAGMENT_COUNT];
    private boolean           isResumed      = false;
    private UiLifecycleHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new UiLifecycleHelper(this, callback);
        helper.onCreate(savedInstanceState);

        FragmentManager manager = getSupportFragmentManager();
        fragments[SPLASH] = manager.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = manager.findFragmentById(R.id.selectionFragment);

        FragmentTransaction tx = manager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            tx.hide(fragments[i]);
        }
        tx.commit();

        /*
         * // start facebook login
         * Session.openActiveSession(this, true, new Session.StatusCallback() {
         * @Override
         * public void call(Session session, SessionState state,
         * Exception exception) {
         * if (session.isOpened()) {
         * Request.newMeRequest(session,
         * new Request.GraphUserCallback() {
         * @Override
         * public void onCompleted(GraphUser user,
         * Response response) {
         * // TODO Auto-generated method stub
         * if (user != null) {
         * TextView welcome = (TextView) findViewById(R.id.welcome);
         * welcome.setText("Hello "
         * + user.getName() + "!");
         * }
         * }
         * }).executeAsync();
         * }
         * }
         * });
         */
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
            showFragment(SELECTION, false);
        } else {
            showFragment(SPLASH, false);
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
                showFragment(SELECTION, false);

            } else if (state.isClosed()) {
                showFragment(SPLASH, false);
            }
        }
    }

    private void showFragment(int index, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == index)
                tx.show(fragments[i]);
            else
                tx.hide(fragments[i]);

        }
        if (addToBackStack) {
            tx.addToBackStack(null);
        }
        tx.commit();

    }

    private Session.StatusCallback callback = new Session.StatusCallback() {

                                                @Override
                                                public void call(
                                                        Session session,
                                                        SessionState state,
                                                        Exception exception) {
                                                    // TODO Auto-generated
                                                    // method stub
                                                    onSessionStateChanged(
                                                            session, state,
                                                            exception);
                                                }
                                            };

}
