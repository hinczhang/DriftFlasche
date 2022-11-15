package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
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


enum BOTTLE_TYPE{
    INFO, MOOD, WARN
}

public class MarkerCreationDialog {
    private MarkerCreationDialog(){}

    private static BOTTLE_TYPE bootleType = BOTTLE_TYPE.INFO; // 1: info, 2: mood, 3: warn
    private static String bootleContent = "";

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
/*
    private static void setDialogText(TextInputLayout textView){
        textView.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        bootleContent = s.toString();
                    }
                }
        );
    }
*/
    public static void create(Context context, String username, Double lat, Double lng, String token) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.set_bottle, null);
        MaterialButtonToggleGroup toogleButton = (MaterialButtonToggleGroup)view.findViewById(R.id.bottleType);
        TextInputLayout dialogText = (TextInputLayout)view.findViewById(R.id.comment);
        setToogleButton(toogleButton);
        //setDialogText(dialogText);
        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.bottle_creation_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    OkHttpClient mOKHttpClient = new OkHttpClient();
                    // TODO: add mediaType, see LoginActivity
                    bootleContent = dialogText.getEditText().getText().toString();
                    RequestBody formBody = new FormBody.Builder()
                            .add("username", username)
                            .add("lat", lat.toString())
                            .add("lng", lng.toString())
                            .add("type", bootleType.toString())
                            .add("content", bootleContent)
                            .add("token", token)
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
                            Log.e("MarkerCreationDialog", "onFailure: " + e.getMessage());
                            Looper.prepare();
                            Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String str = response.body().string();
                            Log.e("MarkerCreationDialog", "onResponse: " + str);
                            if(str != null) {
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
                                    status = receiveObj.getString("status");
                                    msg = receiveObj.getString("msg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                if (Integer.valueOf(status) == 0) {
                                    Looper.prepare();
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }


                            }else{
                                Looper.prepare();
                                Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        }
                    });

                })
                .setNegativeButton(R.string.decline, (DialogInterface.OnClickListener) (dialog, which) -> dialog.dismiss())
                .setView(view)
                .create();
        builder.show();
    }
}
