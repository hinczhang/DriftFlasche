package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.standardserve.driftflasche.R;

public class ProfileDialog {
    private ProfileDialog(){}
    public static void createProfileDialog(Context context, String username, String email){
        LayoutInflater inflater = LayoutInflater.from(context); // Get the layout inflater
        View view = inflater.inflate(R.layout.user_profile, null); // Inflate the layout
        TextView usernameView = view.findViewById(R.id.username_content);
        TextView emailView = view.findViewById(R.id.email_content);
        usernameView.setText(username);
        emailView.setText(email);
        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setTitle("Profile")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Do nothing
                })
                .create();
        builder.show();
    }
}
