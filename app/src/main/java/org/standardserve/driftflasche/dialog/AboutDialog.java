package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.standardserve.driftflasche.R;

public class AboutDialog {
    private AboutDialog(){}

    public static void createAboutDialog(Context context){
        LayoutInflater inflater = LayoutInflater.from(context); // Get the layout inflater
        View view = inflater.inflate(R.layout.about_layout, null); // Inflate the layout
        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setTitle("About")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Do nothing
                })
                .create();
        builder.show();
    }
}
