package com.rnp.zaqzilla.fragments;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.rnp.zaqzilla.R;
import com.rnp.zaqzilla.helper.InternalStorage;
import com.rnp.zaqzilla.helper.SerializaleObject;

public class ProfileFragments extends Fragment {

    private final String           KEY_USER_OBJECT = "1";
    private final String           PROFILE_IMAGE   = "2";

    private TextView               textAbout;
    private ImageView              imgProfile;
    private TextView               textName;
    private TextView               textLocation;
    private TextView               textGenderBirth;
    private UiLifecycleHelper      lifecycleHelper;
    private Session.StatusCallback callback        = new Session.StatusCallback() {

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

        imgProfile = (ImageView) v.findViewById(R.id.imgPofile);

        textName = (TextView) v.findViewById(R.id.selection_user_name);
        textLocation = (TextView) v.findViewById(R.id.location);
        textGenderBirth = (TextView) v.findViewById(R.id.genderAndBirthday);
        textAbout = (TextView) v.findViewById(R.id.about);

        try {

            // get from cache and put to view
            SerializaleObject so = (SerializaleObject) InternalStorage
                    .readObject(getActivity(), KEY_USER_OBJECT);

            textName.setText(so.getName());

            textLocation.setText(so.getLocation());

            textGenderBirth.setText(so.getGender()

            + " " + so.getDate());

            imgProfile.setImageDrawable(InternalStorage.readImage(
                    getActivity(), PROFILE_IMAGE));

            textAbout.setText(so.getAbout());
        }

        catch (IOException e) {

            System.out.println("cahce empty or unaccessible");
        }

        catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        // check required permission to read user data first
        /*
         * List<String> listPermission = session.getPermissions();
         * List<String> requiredPermission = Arrays.asList("user_location",
         * "user_status", "user_birthday", "user_about_me");
         * boolean requestNewPermission = false;
         * for (String string : requiredPermission) {
         * if (!listPermission.contains(string)) {
         * requestNewPermission = true;
         * break;
         * }
         * }
         * if (requestNewPermission && ) {
         * Session.NewPermissionsRequest newPermissionsRequest = new
         * Session.NewPermissionsRequest(
         * this, requiredPermission);
         * session.requestNewReadPermissions(newPermissionsRequest);
         * }
         */

        // request facebook api to get the data
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {

                        if (session == Session.getActiveSession()) {
                            if (user != null) {

                                try {

                                    String url = "http://graph.facebook.com/"
                                            + user.getId()
                                            + "/picture?width=640&height=640";
                                    final SerializaleObject so = new SerializaleObject();
                                    so.setName(user.getName());
                                    so.setAbout(user.getProperty("bio") == null ? ""
                                            : user.getProperty("bio")
                                                    .toString());
                                    so.setDate(user.getBirthday() == null ? ""
                                            : user.getBirthday());
                                    so.setLocation(user.getLocation() == null ? ""
                                            : user.getLocation().getName());
                                    so.setGender(user.getProperty("gender") == null ? null
                                            : user.getProperty("gender")
                                                    .toString());
                                    InternalStorage.writeObject(getActivity(),
                                            KEY_USER_OBJECT, so);
                                    UrlImageViewHelper.setUrlDrawable(
                                            imgProfile, url,
                                            new UrlImageViewCallback() {

                                                @Override
                                                public void onLoaded(
                                                        ImageView arg0,
                                                        Bitmap arg1,
                                                        String arg2,
                                                        boolean arg3) {
                                                    so.setBitmap(arg1);
                                                    try {
                                                        InternalStorage
                                                                .writeBitmap(
                                                                        getActivity(),
                                                                        PROFILE_IMAGE,
                                                                        arg1);

                                                    } catch (IOException e) {
                                                        // TODO Auto-generated
                                                        // catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                    textName.setText(user.getName());

                                    textLocation
                                            .setText(user.getLocation() == null ? ""
                                                    : user.getLocation()
                                                            .getName());
                                    String gender = user.getProperty("gender")
                                            .toString();
                                    String birthday = user.getBirthday() == null ? ""
                                            : user.getBirthday();
                                    textGenderBirth.setText(gender + " "
                                            + birthday);

                                    textAbout
                                            .setText(user.getProperty("bio") == null ? ""
                                                    : user.getProperty("bio")
                                                            .toString());
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
