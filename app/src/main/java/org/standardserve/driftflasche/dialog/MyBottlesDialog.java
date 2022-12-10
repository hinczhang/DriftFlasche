package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.standardserve.driftflasche.R;

// User bottles management dialog
public class MyBottlesDialog {
    private MyBottlesDialog(){}

    // Show dialog
    public static void showMyBottlesDialog(Context context, String token, JSONArray bottles, double kilometersDistance, double latitude, double longitude, String username, GoogleMap map) throws JSONException {
        LayoutInflater inflater = LayoutInflater.from(context); // Get layout inflater
        View view = inflater.inflate(R.layout.my_bottles, null); // Inflate layout

        RecyclerView myBottlesTable = view.findViewById(R.id.myBottlesRecyclerView); // Get recycler view

        myBottlesTable.setLayoutManager(new LinearLayoutManager(context)); // Set layout manager
        myBottlesTable.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL)); // Set item decoration
        BottleAdapter adapter = new BottleAdapter(bottles, token, context, kilometersDistance, latitude, longitude, username, map); // Create adapter
        myBottlesTable.setAdapter(adapter); // Set adapter

        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .create();

        builder.show();
    }
}
