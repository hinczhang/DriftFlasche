package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MarkerInfomationDialog {
    private MarkerInfomationDialog(){}
    public static void showMarkerInfomationDialog(Context context, JSONObject markerContent, String token, String own_username) throws JSONException {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.info_bottle, null);


        RecyclerView commentTable = view.findViewById(R.id.comments);
        commentTable.setLayoutManager(new LinearLayoutManager(context));
        commentTable.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        JSONArray comments = markerContent.getJSONArray("affiliate");
        CommentAdapter adapter = new CommentAdapter(comments);
        commentTable.setAdapter(adapter);

        ImageView icon = view.findViewById(R.id.typeIcon);
        String type = markerContent.getString("type");
        icon.setImageResource(type.equals("INFO") ? R.drawable.info_icon : type.equals("MOOD") ? R.drawable.mood_icon : R.drawable.warn_icon);

        TextView userTitle = view.findViewById(R.id.user_title);
        String username = markerContent.getString("username");
        userTitle.setText(username);

        TextInputLayout contentInput = view.findViewById(R.id.content_info);
        Objects.requireNonNull(contentInput.getEditText()).setText(markerContent.getString("content"));
        contentInput.setEnabled(false);

        TextInputLayout commentInput = view.findViewById(R.id.comment);


        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton(R.string.submission, (dialog, which) ->{
                    OkHttpClient client = new OkHttpClient();
                    try {
                        RequestBody requestBody = new FormBody.Builder()
                                .add("id", markerContent.getString("_id"))
                                .add("content", Objects.requireNonNull(commentInput.getEditText()).getText().toString())
                                .add("username", own_username)
                                .add("token", token)
                                .add("mode", "comment")
                                .build();
                        String url = "http://138.68.65.184:5000/api/bottle";
                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        Call call = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Looper.prepare();
                                Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                String str = Objects.requireNonNull(response.body()).string();
                                if(str.length()>0) {
                                    JSONObject receiveObj = null;
                                    try {
                                        receiveObj = new JSONObject(str);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    String status = null;
                                    String msg = null;
                                    try {
                                        assert receiveObj != null;
                                        status = receiveObj.getString("status");
                                        msg = receiveObj.getString("msg");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Looper.prepare();
                                        Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                    assert status != null;
                                    if(Integer.parseInt(status) == 0){
                                        JSONObject tmp = new JSONObject();
                                        try {
                                            tmp.put("username", own_username);
                                            tmp.put("content", commentInput.getEditText().getText().toString());
                                            markerContent.getJSONArray("affiliate").put(tmp);
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                try {
                                                    adapter.notifyItemInserted(markerContent.getJSONArray("affiliate").length() - 1);
                                                    adapter.notifyItemRangeChanged(markerContent.getJSONArray("affiliate").length() - 1, markerContent.getJSONArray("affiliate").length());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    Looper.prepare();
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .setNeutralButton(R.string.decline, (dialog, which) -> {

                })
                .create();

        builder.show();
    }
}


