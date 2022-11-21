package org.standardserve.driftflasche.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import org.standardserve.driftflasche.R;

public class MarkerSettingDialog {
    private MarkerSettingDialog() {}

    @SuppressLint("DefaultLocale")
    public static void createMarkerSettingDialog(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.setting_bottle, null);

        Slider rangeSlider = view.findViewById(R.id.range_slider);
        rangeSlider.setLabelFormatter(value -> {
            String expression;
            if(value < 100){
                expression = String.format("%.0f km", value);
            }else{
                expression = String.format("100+ km");
            }
            return expression;
        });


        AlertDialog builder = new MaterialAlertDialogBuilder(context)
                .setView(view)
                .setPositiveButton(R.string.submission, (dialog, which) -> {

                })
                .setNeutralButton(R.string.decline, (dialog, which) -> {

                })
                .create();
        builder.show();
    }
}
