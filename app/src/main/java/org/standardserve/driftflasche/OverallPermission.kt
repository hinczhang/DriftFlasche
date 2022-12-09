package org.standardserve.driftflasche

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object OverallPermission {
    const val LOCATION_PERMISSION_REQUEST_CODE = 1
    const val READ_EXTERNAL_STORAGE_REQUEST_CODE = 2
    const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3
    /*
   * Enable the external read and write permission
   * */

    fun enableExternalStoragePermission(context: Context){
        // Check if the READ_EXTERNAL_STORAGE permission is already available.
        val writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        }
        // Check if the WRITE_EXTERNAL_STORAGE permission is already available.
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (readPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST_CODE)
        }
    }

    fun enableLocationPermission(context: Context){
        // Check if the FINE_LOCATION and COARSE_LOCATION permissions are already available.
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Display a dialog with rationale.
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }



}