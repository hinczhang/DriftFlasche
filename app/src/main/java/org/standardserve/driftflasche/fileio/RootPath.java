package org.standardserve.driftflasche.fileio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

// Class: RootPath (static)
@SuppressLint("StaticFieldLeak")
public class RootPath {
    private RootPath(){}

    // Method: getRootPath (static)
    private static Context context = null;

    // set context
    public static void setContext(Context external_context) {
        context = external_context;
    }

    // get cache path
    public static String getCacheDir(){
        return context.getCacheDir().getAbsolutePath();
    }

    // get file path
    public static String getFileDir(){
        return context.getFilesDir().getAbsolutePath();
    }

    // get external storage path
    public static String getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    // get external storage public path
    public static String getExternalStoragePublicDirectory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    // get external file path
    public static String getExternalFilesDir(){
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    // get external cache path
    public static String getExternalCacheDir(){
        return context.getExternalCacheDir().getAbsolutePath();
    }
}