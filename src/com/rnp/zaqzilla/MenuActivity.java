package com.rnp.zaqzilla;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.rnp.zaqzilla.fragments.ProfileFragments;
import com.rnp.zaqzilla.fragments.ShareFragment;

public class MenuActivity extends FragmentActivity {
    private DrawerLayout          drawerLayout;
    private ListView              listViewDrawer;
    private ActionBarDrawerToggle toggle;
    private boolean               isResumed = false;
    private UiLifecycleHelper     helper;

    public enum Fragments {
        PROFILE("Profile"), SHARE("Share to Fb"), TWITTER("Twitter Timeline"), LOGOUT(
                "Logout");

        private final String value;

        private Fragments(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setUi();

        helper = new UiLifecycleHelper(this, callback);
        helper.onCreate(savedInstanceState);
        showFragment(Fragments.PROFILE);

    }

    private void showFragment(Fragments fragments) {
        Fragment f;
        switch (fragments) {
            case PROFILE:
                f = new ProfileFragments();

                break;
            case SHARE:
                f = new ShareFragment();
                break;

            default:
                f = new ProfileFragments();
                break;
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, f).commit();
        drawerLayout.closeDrawer(listViewDrawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void setUi() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listViewDrawer = (ListView) findViewById(R.id.left_drawer);
        List<String> menuString = new ArrayList<String>();
        System.out.println(Fragments.values());
        for (Fragments frag : Fragments.values()) {
            System.out.println("add value " + frag.getValue());
            menuString.add(frag.getValue());
        }
        System.out.println(menuString.toString());
        listViewDrawer.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menuString) {
            public View getView(int pos, View v, ViewGroup p) {

                View vx = super.getView(pos, v, p);
                TextView tv = (TextView) vx.findViewById(android.R.id.text1);

                tv.setTextColor(Color.BLUE);
                return tv;

            }

        });
        listViewDrawer.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                    case 0:
                        showFragment(Fragments.PROFILE);
                        break;
                    case 1:
                        showFragment(Fragments.SHARE);
                        break;
                    case 2:
                        showFragment(Fragments.TWITTER);
                        break;
                    case 3:
                        Session.getActiveSession()
                                .closeAndClearTokenInformation();
                        MenuActivity.this.finish();
                        break;
                    default:
                        break;
                }

            }
        });

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.icon,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                getActionBar().setTitle("Title");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu();
            }

        };
        drawerLayout.setDrawerListener(toggle);

    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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

    private void onSessionStateChanged(Session session, SessionState state,
            Exception exception) {
        if (isResumed) {

            if (state.isOpened()) {

            } else if (state.isClosed()) {

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }
        }
    }

}
