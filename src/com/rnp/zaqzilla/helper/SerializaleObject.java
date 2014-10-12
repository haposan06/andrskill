package com.rnp.zaqzilla.helper;

import java.io.Serializable;

import android.graphics.Bitmap;

public class SerializaleObject implements Serializable {

    /**
         * 
         */
    private static final long serialVersionUID = -4428570315647596530L;

    private String            name;
    private String            location;
    private String            gender;
    private String            date;
    private Bitmap            bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private String about;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
