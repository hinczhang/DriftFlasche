package org.standardserve.driftflasche.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import org.standardserve.driftflasche.R;

import java.util.ArrayList;
import java.util.List;

public class MarkerSettingDialog {
    private MarkerSettingDialog() {}
    @SuppressLint("DefaultLocale")
    public static void createMarkerSettingDialog(Context context, double lat, double lng, String token, String username, GoogleMap map){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setting_bottle, null);


        String[] id_types = {"INFO", "MOOD", "WARN"};

        Slider rangeSlider = view.findViewById(R.id.range_slider);
        rangeSlider.setLabelFormatter(value -> {
            String expression;
            if(value < 100){
                expression = String.format("%.0f km", value);
            }else{
                expression = "100+ km";
            }
            return expression;
        });

        ChipGroup chipGroup = view.findViewById(R.id.bottleType_chipGroup);


        @SuppressLint("NonConstantResourceId") AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton(R.string.submission, (dialog, which) -> {

                    List<Integer> ids = chipGroup.getCheckedChipIds();
                    Log.d("ids", ids.toString());
                    if(ids.size() == 0){
                        //Looper.prepare();
                        Toast.makeText(context, "Require at least one type!", Toast.LENGTH_SHORT).show();
                        //Looper.loop();
                    }else {
                        StringBuilder type_string = new StringBuilder();
                        for (int i = 0; i < ids.size(); i++) {
                            switch (ids.get(i)) {
                                case R.id.chip_info:
                                    type_string.append(id_types[0]);
                                    break;
                                case R.id.chip_mood:
                                    type_string.append(id_types[1]);
                                    break;
                                case R.id.chip_warn:
                                    type_string.append(id_types[2]);
                                    break;
                                default:
                                    break;
                            }
                            //type_string.append(id_types[ids.get(i)]);
                            if (i != ids.size() - 1) {
                                type_string.append(";");
                            }
                        }

                        bottlesReload.loadBottlesbyDistance(context, rangeSlider.getValue() ,lat, lng, token, username, map, type_string.toString());
                    }


                })
                .setNeutralButton(R.string.decline, (dialog, which) -> {

                })
                .create();
        builder.show();
    }
}
