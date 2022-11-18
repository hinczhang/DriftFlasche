package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.standardserve.driftflasche.R;

public class MarkerInfomationDialog {
    private MarkerInfomationDialog(){}
    public static void showMarkerInfomationDialog(Context context, JSONObject markerContent) throws JSONException {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.info_bottle, null);


        RecyclerView commentTable = view.findViewById(R.id.comments);
        commentTable.setLayoutManager(new LinearLayoutManager(context));
        commentTable.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        CommentAdapter adapter = new CommentAdapter(markerContent.getJSONArray("affiliate"));
        commentTable.setAdapter(adapter);

        ImageView icon = view.findViewById(R.id.typeIcon);
        String type = markerContent.getString("type");
        icon.setImageResource(type.equals("INFO") ? R.drawable.info_icon : type.equals("MOOD") ? R.drawable.mood_icon : R.drawable.warn_icon);

        TextView userTitle = view.findViewById(R.id.user_title);
        String username = markerContent.getString("username");
        userTitle.setText(username);

        TextInputLayout contentInput = view.findViewById(R.id.content_info);
        contentInput.getEditText().setText(markerContent.getString("content"));
        contentInput.setEnabled(false);


        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .create();

        builder.show();
    }
}


