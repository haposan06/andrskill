package com.rnp.zaqzilla.fragments;

import java.text.SimpleDateFormat;

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
    private TextView               textName;
    private TextView               textLocation;
    private TextView               textGenderBirth;
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
        pictureView.setPresetSize(ProfilePictureView.CUSTOM);
        pictureView.setCropped(true);

        textName = (TextView) v.findViewById(R.id.selection_user_name);
        textLocation = (TextView) v.findViewById(R.id.location);
        textGenderBirth = (TextView) v.findViewById(R.id.genderAndBirthday);

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

                                try {
                                    SimpleDateFormat format = new SimpleDateFormat(
                                            "DD mm yyyy");

                                    pictureView.setProfileId(user.getId());
                                    textName.setText(user.getName());
                                    /*
                                     * textLocation.setText(user.getLocation()
                                     * .getName());
                                     */
                                    textGenderBirth.setText(user.getProperty(
                                            "gender").toString());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        if (response.getError() != null) {

                        }
                    }

                });
        request.executeAsync();

    }

}
