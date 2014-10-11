package com.rnp.zaqzilla.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.rnp.zaqzilla.R;

public class ProfileFragments extends Fragment {

    private ProfilePictureView     pictureView;
    private TextView               textView;
    private UiLifecycleHelper      lifecycleHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {

                                                @Override
                                                public void call(
                                                        Session session,
                                                        SessionState state,
                                                        Exception exception) {
                                                    onSessionStateChange(
                                                            session, state,
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
        View v = inflater.inflate(R.layout.profile, container, false);

        pictureView = (ProfilePictureView) v
                .findViewById(R.id.selection_profile_pic);
        pictureView.setCropped(true);

        textView = (TextView) v.findViewById(R.id.selection_user_name);

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            getMe(session);
        }

        return v;
    }

    public void onSessionStateChange(final Session session, SessionState state,
            Exception exception) {
        if (session != null && session.isOpened()) {
            getMe(session);
        }

    }

    private void getMe(final Session session) {
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                pictureView.setProfileId(user.getId());
                                textView.setText(user.getName());
                            }
                        }
                        if (response.getError() != null) {

                        }
                    }

                });
        request.executeAsync();

    }

}
