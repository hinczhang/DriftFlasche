package org.standardserve.driftflasche.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.standardserve.driftflasche.R;
import org.standardserve.driftflasche.network.NetworkAccess;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Load bottles from server with certain parameters
public class bottlesReload {
    public static void loadBottlesbyDistance(Context context, double kilometersDistance, double latitude, double longitude, String token, String username, GoogleMap map, String types){
        OkHttpClient mOKHttpClient = new OkHttpClient();
        RequestBody formBody;
        if(Objects.equals(types, "")){
            formBody = new FormBody.Builder()
                    .add("lat", String.valueOf(latitude))
                    .add("lng", String.valueOf(longitude))
                    .add("distance", String.valueOf(kilometersDistance))
                    .add("token", token)
                    .add("username", username)
                    .add("mode", "search")
                    .build();
        }else{
            formBody = new FormBody.Builder()
                    .add("lat", String.valueOf(latitude))
                    .add("lng", String.valueOf(longitude))
                    .add("distance", String.valueOf(kilometersDistance))
                    .add("token", token)
                    .add("username", username)
                    .add("mode", "search")
                    .add("types", types)
                    .build();
        }
        String url = NetworkAccess.getBOTTLEAccess();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = mOKHttpClient.newCall(request);
        call.enqueue(new Callback(){
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Get bottles from server
                String str = Objects.requireNonNull(response.body()).string();
                JSONObject receiveObj = null;
                try {
                    receiveObj = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                JSONArray loading_data = null;
                //String msg = null;
                try {
                    assert receiveObj != null;
                    loading_data = receiveObj.getJSONArray("data");
                    //msg = receiveObj.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                JSONArray finalLoading_data = loading_data;
                // render bottles on map within the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    map.clear();
                    // add bottles to map with markers
                    for(int i = 0; i < Objects.requireNonNull(finalLoading_data).length(); ++i) {
                        try {
                            JSONObject bottle = finalLoading_data.getJSONObject(i);
                            String bottleType = bottle.getString("type");
                            String bottleContent = bottle.getString("content");
                            //String bottleUsername = bottle.getString("username");
                            String bottleLat = bottle.getJSONObject("position").getString("lat");
                            String bottleLng = bottle.getJSONObject("position").getString("lng");
                            int bottleIcon;
                            switch(bottleType){
                                case "MOOD": bottleIcon = R.drawable.mood_driftbottle; break;
                                case "WARN": bottleIcon = R.drawable.warn_driftbottle; break;
                                default: bottleIcon = R.drawable.info_driftbottle; break;
                            }

                            Marker marker =  map.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(bottleLat), Double.parseDouble(bottleLng)))
                                    .title(bottleType)
                                    .snippet(bottleContent)
                                    .icon(BitmapDescriptorFactory.fromResource(bottleIcon)));
                            assert marker != null;
                            marker.setTag(bottle);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Looper.prepare();
                Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });

    }
}
