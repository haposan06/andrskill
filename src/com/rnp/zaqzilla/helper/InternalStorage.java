package com.rnp.zaqzilla.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public final class InternalStorage {

    private InternalStorage() {
    }

    public static void clear(Context context) {
        File dir = context.getFilesDir();
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }

    }

    public static void writeObject(Context context, String key, Object object)
            throws IOException {
        FileOutputStream fos = context
                .openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream stream = new ObjectOutputStream(fos);
        stream.writeObject(object);
        stream.close();
        fos.close();
    }

    public static void writeBitmap(Context context, String key, Bitmap bitmap)
            throws FileNotFoundException, IOException {
        FileOutputStream fos = context
                .openFileOutput(key, Context.MODE_PRIVATE);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
        fos.close();

    }

    public static Object readObject(Context context, String key)
            throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream stream = new ObjectInputStream(fis);
        Object object = stream.readObject();
        return object;
    }

    public static Drawable readImage(Context context, String key)
            throws FileNotFoundException, IOException {
        File path = context.getFileStreamPath(key);
        return Drawable.createFromPath(path.toString());
    }

}
