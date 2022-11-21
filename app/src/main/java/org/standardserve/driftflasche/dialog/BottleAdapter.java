package org.standardserve.driftflasche.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.standardserve.driftflasche.R;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;




public class BottleAdapter extends RecyclerView.Adapter<BottleAdapter.ViewHolder> {
    private static Marker marker = null;
    private final JSONArray bottles;
    private final String token;

    private final Context context;
    private final double kilometersDistance;
    private final double latitude;
    private final double longitude;
    private final String username;
    private final GoogleMap map;

    BottleAdapter(JSONArray out_bottles, String out_token, Context out_context, double out_kilometersDistance, double out_latitude, double out_longitude, String out_username, GoogleMap out_map){

        bottles = out_bottles;
        token = out_token;
        context = out_context;
        kilometersDistance = out_kilometersDistance;
        latitude = out_latitude;
        longitude = out_longitude;
        username = out_username;
        map = out_map;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.bottles_table, null);
        return new BottleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.seqNum.setText(String.valueOf(position+1));

        try {
            holder.abstractContent.setText(bottles.getJSONObject(position).getString("content"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String id = bottles.getJSONObject(position).getString("_id");
            double target_lat = Double.parseDouble(bottles.getJSONObject(position).getJSONObject("position").getString("lat"));
            double target_lng = Double.parseDouble(bottles.getJSONObject(position).getJSONObject("position").getString("lng"));

            holder.locButton.setOnClickListener(v-> new Handler(Looper.getMainLooper()).post(() -> {
                LatLng furtherPoint = new LatLng(target_lat, target_lng);
                map.moveCamera(CameraUpdateFactory.newLatLng(furtherPoint));
                map.moveCamera(CameraUpdateFactory.zoomTo(18F));
                // TODO: add a marker? but how to remove the marker? a global static array or variable to hold the marker?
                if(marker!=null){
                    marker.remove();
                }

                marker = map.addMarker(new com.google.android.gms.maps.model.MarkerOptions().position(furtherPoint).title("Your Bottle"));

            }));


            holder.deleteButton.setOnClickListener(v -> {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("token", token)
                        .add("id", id)
                        .add("mode", "delete")
                        .add("username", username)
                        .build();
                String url = "http://138.68.65.184:5000/api/bottle";
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {
                        Looper.prepare();
                        Toast.makeText(context, "Login failed due to request error", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
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
                                bottles.remove(position);

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, bottles.length());
                                });
                                bottlesReload.loadBottlesbyDistance(context, kilometersDistance, latitude, longitude, token, username,map, "");


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
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return bottles.length();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView seqNum;
        private final TextView abstractContent;
        private final MaterialButton deleteButton;
        private final MaterialButton locButton;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            seqNum = itemView.findViewById(R.id.seqNum);
            abstractContent = itemView.findViewById(R.id.abstract_content);
            deleteButton = itemView.findViewById(R.id.delete_icon);
            locButton = itemView.findViewById(R.id.loc_bottle);
        }
    }
}
