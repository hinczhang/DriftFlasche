package org.standardserve.googletestunit.fileio

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import org.standardserve.driftflasche.MapsActivity

@SuppressLint("StaticFieldLeak")
object RootPath {
    private lateinit var context: Context

    fun setContext(context: Context) {
        this.context = context
    }

    fun getCacheDir() = context.cacheDir.absolutePath
    fun getFileDir() = context.filesDir.absolutePath
    fun getExternalStorageDirectory() = Environment.getExternalStorageDirectory().absolutePath
    fun getExternalStoragePublicDirectory() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
    fun getExternalFilesDir() = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath
    fun getExternalCacheDir() = context.externalCacheDir?.absolutePath
}