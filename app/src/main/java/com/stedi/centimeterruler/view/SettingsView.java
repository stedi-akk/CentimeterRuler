package com.stedi.centimeterruler.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.stedi.centimeterruler.App;
import com.stedi.centimeterruler.BuildConfig;
import com.stedi.centimeterruler.Constants;
import com.stedi.centimeterruler.R;
import com.stedi.centimeterruler.Settings;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsView extends FrameLayout {
    @BindView(R.id.settings_view_tv_calibration)
    TextView tvCalibration;

    @BindView(R.id.settings_view_tv_version_info)
    TextView tvVersion;

    @BindView(R.id.settings_view_btn_show)
    ImageView btnShow;

    @BindView(R.id.settings_view_calibration_bar)
    CalibrationBar calibrationBar;

    @BindView(R.id.settings_view_color_picker)
    ColorPicker colorPicker;

    public SettingsView(Context context) {
        this(context, null);
    }

    public SettingsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.settings_view, this, true);
        ButterKnife.bind(this);
        tvCalibration.setTextColor(Color.BLACK);
        tvVersion.setTextColor(Color.BLACK);
        tvVersion.setText("v" + BuildConfig.VERSION_NAME);
        updateColors(Settings.getInstance().getTheme());
        calibrationBar.setMax(Constants.MAX_CALIBRATION);
        calibrationBar.setProgress(Settings.getInstance().getCalibration());
        showCalibrationValue(Settings.getInstance().getCalibration());
        for (Settings.Theme theme : Settings.Theme.values())
            colorPicker.addPicker(theme.rulerColor, theme.elementsColor);
        colorPicker.setSelected(Settings.getInstance().getTheme().ordinal());
    }

    public void onStart() {
        App.getBus().register(this);
    }

    public void onStop() {
        App.getBus().unregister(this);
    }

    @Subscribe
    public void onCalibrationChange(CalibrationBar.OnChange onChange) {
        showCalibrationValue(onChange.value);
    }

    @Subscribe
    public void onPickerSelected(ColorPicker.OnSelected onSelected) {
        updateColors(Settings.Theme.values()[onSelected.index]);
    }

    private void showCalibrationValue(int value) {
        tvCalibration.setText(String.valueOf(value));
    }

    private void updateColors(Settings.Theme theme) {
        btnShow.setColorFilter(theme.elementsColor);
        calibrationBar.setColor(theme.elementsColor);
    }
}
