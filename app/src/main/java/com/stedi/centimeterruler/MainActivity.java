package com.stedi.centimeterruler;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Subscribe;
import com.stedi.centimeterruler.view.CalibrationBar;
import com.stedi.centimeterruler.view.ColorPicker;
import com.stedi.centimeterruler.view.RulerView;
import com.stedi.centimeterruler.view.SettingsView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String TAG_SETTINGS_CHANGED = "TAG_SETTINGS_CHANGED";
    private final String TAG_CALIBRATION_INFO = "TAG_CALIBRATION_INFO";

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
        rulerView.setRulerColor(Settings.getInstance().getTheme().rulerColor);
        setNavBarColor(Settings.getInstance().getTheme().elementsColor);
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
        rulerView.setRulerColor(Settings.getInstance().getTheme().rulerColor);
        setNavBarColor(Settings.getInstance().getTheme().elementsColor);
        settingsView.refresh();
    }

    @Subscribe
    public void onSettingsChanged(SettingsView.OnSettingsChanged onSettingsChanged) {
        SimpleDialog dlg = SimpleDialog.newInstance(getString(R.string.apply_settings), null,
                getString(R.string.dialog_ok), getString(R.string.dialog_cancel));
        dlg.show(getSupportFragmentManager(), TAG_SETTINGS_CHANGED);
    }

    @Subscribe
    public void onOkClick(SimpleDialog.OnResult onResult) {
        if (onResult.tag.equals(TAG_SETTINGS_CHANGED)) {
            if (onResult.okClicked) {
                Settings.getInstance().commit();
            } else {
                Settings.getInstance().revert();
                settingsView.refresh();
                rulerView.calibrate(Settings.getInstance().getCalibration());
                rulerView.setRulerColor(Settings.getInstance().getTheme().rulerColor);
                setNavBarColor(Settings.getInstance().getTheme().elementsColor);
            }
            settingsView.showSettings(false, true);
        } else if (onResult.tag.equals(TAG_CALIBRATION_INFO)) {

        }
    }

    private void setNavBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setNavigationBarColor(color);
    }
}
