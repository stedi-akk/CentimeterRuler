package com.stedi.centimeterruler;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.stedi.centimeterruler.view.CalibrationBar;
import com.stedi.centimeterruler.view.ColorPicker;
import com.stedi.centimeterruler.view.RulerView;
import com.stedi.centimeterruler.view.SettingsView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    @BindView(R.id.settings_view)
    SettingsView settingsView;

    @BindView(R.id.ruler_view)
    RulerView rulerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        rulerView.calibrate(Settings.getInstance().getCalibration());
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.getBus().unregister(this);
        Settings.getInstance().save();
    }

    @Subscribe
    public void onCalibrationChange(CalibrationBar.OnChange onChange) {
        Settings.getInstance().setCalibration(onChange.value);
        rulerView.calibrate(onChange.value);
        settingsView.refresh();
    }

    @Subscribe
    public void onPickerSelected(ColorPicker.OnSelected onSelected) {
        Settings.getInstance().setTheme(Settings.Theme.values()[onSelected.index]);
        rulerView.setBackgroundColor(Settings.getInstance().getTheme().rulerColor);
        settingsView.refresh();
    }
}
