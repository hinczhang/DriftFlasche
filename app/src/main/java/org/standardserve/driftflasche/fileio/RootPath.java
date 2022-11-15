package org.standardserve.driftflasche.fileio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

@SuppressLint("StaticFieldLeak")
public class RootPath {

    private RootPath(){}
    private static Context context = null;

    public static void setContext(Context external_context) {
        context = external_context;
    }

    public static String getCacheDir(){
        return context.getCacheDir().getAbsolutePath();
    }

    public static String getFileDir(){
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getExternalStorageDirectory(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getExternalStoragePublicDirectory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    public static String getExternalFilesDir(){
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    public static String getExternalCacheDir(){
        return context.getExternalCacheDir().getAbsolutePath();
    }
}