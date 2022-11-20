package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.util.Log;
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

public class MyBottlesDialog {
    private MyBottlesDialog(){}
    public static void showMyBottlesDialog(Context context, String token, JSONArray bottles, double kilometersDistance, double latitude, double longitude, String username, GoogleMap map) throws JSONException {
        // TODO: Change to the table view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_bottles, null);

        RecyclerView myBottlesTable = view.findViewById(R.id.myBottlesRecyclerView);

        myBottlesTable.setLayoutManager(new LinearLayoutManager(context));
        myBottlesTable.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        BottleAdapter adapter = new BottleAdapter(bottles, token, context, kilometersDistance, latitude, longitude, username, map);
        myBottlesTable.setAdapter(adapter);

        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .create();

        builder.show();
    }
}
