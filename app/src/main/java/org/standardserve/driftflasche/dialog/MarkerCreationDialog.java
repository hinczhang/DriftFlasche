package org.standardserve.driftflasche.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.standardserve.driftflasche.R;

import java.io.IOException;
import java.util.Objects;


enum BOTTLE_TYPE{
    INFO, MOOD, WARN
}

public class MarkerCreationDialog {
    private MarkerCreationDialog(){}

    private static BOTTLE_TYPE bootleType = BOTTLE_TYPE.INFO; // 1: info, 2: mood, 3: warn
    private static String bootleContent = "";

    @SuppressLint("NonConstantResourceId")
    private static void setToogleButton(MaterialButtonToggleGroup toogleButton){
        toogleButton.addOnButtonCheckedListener(
                (group, checkedId, isChecked) -> {
                    if(isChecked){
                        switch (checkedId){
                            case R.id.info: bootleType = BOTTLE_TYPE.INFO; break;
                            case R.id.mood: bootleType = BOTTLE_TYPE.MOOD; break;
                            case R.id.warn: bootleType = BOTTLE_TYPE.WARN; break;
                            default: break;
                        }
                    }
                }
        );
    }

    public static void create(Context context, String username, Double lat, Double lng, String token, GoogleMap map) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.set_bottle, null);
        MaterialButtonToggleGroup toogleButton = view.findViewById(R.id.bottleType);
        TextInputLayout dialogText = view.findViewById(R.id.comment);
        setToogleButton(toogleButton);
        //setDialogText(dialogText);
        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.bottle_creation_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    OkHttpClient mOKHttpClient = new OkHttpClient();
                    // TODO: add mediaType, see LoginActivity
                    bootleContent = Objects.requireNonNull(dialogText.getEditText()).getText().toString();
                    RequestBody formBody = new FormBody.Builder()
                            .add("username", username)
                            .add("lat", lat.toString())
                            .add("lng", lng.toString())
                            .add("type", bootleType.toString())
                            .add("content", bootleContent)
                            .add("token", token)
                            .add("mode", "add")
                            .build();
                    String url = "http://138.68.65.184:5000/api/bottle";
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    Call call = mOKHttpClient.newCall(request);
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
                                    bottlesReload.loadBottlesbyDistance(context, 20, lat, lng, token, username,map);
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

                })
                .setNegativeButton(R.string.decline, (dialog, which) -> dialog.dismiss())
                .setView(view)
                .create();
        builder.show();
    }
}
