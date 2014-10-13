package com.rnp.zaqzilla.fragments;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.rnp.zaqzilla.R;

public class ShareFragment extends Fragment {

    private EditText                  txtStatus;

    private EditText                  txtLink;

    private Button                    button;

    private static final List<String> PERMISSIONS                   = Arrays.asList("publish_actions");
    private static final String       PENDING_PUBLISH_KEY           = "pendingPublishReauthorization";
    private boolean                   pendingPublishReauthorization = false;
    private UiLifecycleHelper         lifecycleHelper;

    private Session.StatusCallback    callback                      = new Session.StatusCallback() {

                                                                        @Override
                                                                        public
                                                                                void
                                                                                call(Session session,
                                                                                        SessionState state,
                                                                                        Exception exception) {
                                                                            onSessionStateChange(
                                                                                    session,
                                                                                    state,
                                                                                    exception);

                                                                        }
                                                                    };

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        lifecycleHelper = new UiLifecycleHelper(getActivity(), callback);
        lifecycleHelper.onCreate(state);
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleHelper.onResume();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);

        lifecycleHelper.onSaveInstanceState(bundle);
    }

    public void onPause() {
        super.onPause();
        lifecycleHelper.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        lifecycleHelper.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.share, container, false);
        if (savedInstanceState != null) {
            pendingPublishReauthorization = savedInstanceState.getBoolean(
                    PENDING_PUBLISH_KEY, false);
        }

        txtStatus = (EditText) v.findViewById(R.id.txtStatus);
        txtLink = (EditText) v.findViewById(R.id.txtLink);
        button = (Button) v.findViewById(R.id.btnShare);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                button.setEnabled(false);
                publish();
            }
        });
        return v;
    }

    private void publish() {
        Session session = Session.getActiveSession();

        if (session != null) {
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                try {
                    pendingPublishReauthorization = true;
                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
                            getActivity(), PERMISSIONS);

                    session.requestNewPublishPermissions(newPermissionsRequest);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString("name", txtStatus.getText().toString());
            postParams.putString("link", txtLink.getText().toString());

            Request.Callback callback = new Request.Callback() {

                @Override
                public void onCompleted(Response response) {
                    JSONObject json = response.getGraphObject()
                            .getInnerJSONObject();

                    String postId = null;
                    try {
                        postId = json.getString("id");
                    } catch (Exception e) {
                        System.out.println("JSON ERROR" + e.getMessage());
                    }

                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(getActivity(), error.getErrorMessage(),
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(),
                                "Success share to facebook", Toast.LENGTH_LONG)
                                .show();

                    }
                    button.setEnabled(true);

                }
            };

            Request request = new Request(session, "me/feed", postParams,
                    HttpMethod.POST, callback);
            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }
    }

    private boolean isSubsetOf(Collection<String> subset,
            Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    public void onSessionStateChange(final Session session, SessionState state,
            Exception exception) {
        if (session != null && session.isOpened()) {

            if (pendingPublishReauthorization
                    && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                pendingPublishReauthorization = false;
                publish();

            }

        }

    }

}
