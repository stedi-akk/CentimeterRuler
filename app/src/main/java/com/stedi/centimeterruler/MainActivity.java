package com.stedi.centimeterruler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;
import com.stedi.centimeterruler.view.CalibrationBar;
import com.stedi.centimeterruler.view.ColorPicker;
import com.stedi.centimeterruler.view.RulerView;
import com.stedi.centimeterruler.view.SettingsView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements OnTouchListener {
    private static final String CALIBRATION_VALUE = "calibration_value";

    @BindView(R.id.settings_view)
    SettingsView settingsView;
    private RulerView rulerView;
    private FrameLayout.LayoutParams rulerParams;
    private SharedPreferences preferences;

    private int screenWidth;
    private int xPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        preferences = getPreferences(MODE_PRIVATE);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        rulerView = (RulerView) findViewById(R.id.ruler_view);
        rulerView.setOnTouchListener(this);
        rulerView.calibrate(loadCalibration(), 60);
        rulerParams = (FrameLayout.LayoutParams) rulerView.getLayoutParams();
        rulerParams.leftMargin = screenWidth / 2;
    }

    @Override
    protected void onStart() {
        super.onStart();
        settingsView.onStart();
        App.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        settingsView.onStop();
        App.getBus().unregister(this);
        Settings.getInstance().save();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xPosition = x - rulerParams.leftMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                int xMove = x - xPosition;
                if (xMove <= 0)
                    rulerParams.leftMargin = 0;
                else if (xMove + rulerView.getWidth() >= screenWidth)
                    rulerParams.leftMargin = screenWidth - rulerView.getWidth();
                else
                    rulerParams.leftMargin = xMove;
                rulerView.setLayoutParams(rulerParams);
                break;
            default:
                break;
        }
        return true;
    }

    @Subscribe
    public void onCalibrationChange(CalibrationBar.OnChange onChange) {
        Settings.getInstance().setCalibration(onChange.value);
        rulerView.calibrate(Constants.MAX_CALIBRATION - onChange.value, Constants.MAX_CALIBRATION);
    }

    @Subscribe
    public void onPickerSelected(ColorPicker.OnSelected onSelected) {
        Settings.getInstance().setRulerColor(Settings.RulerColor.values()[onSelected.index]);
        rulerView.setBackgroundColor(Settings.getInstance().getRulerColor().color);
    }

    private void saveCalibration() {
        Editor editor = preferences.edit();
        editor.putInt(CALIBRATION_VALUE, 0);
        editor.commit();
    }

    private int loadCalibration() {
        return preferences.getInt(CALIBRATION_VALUE, 20);
    }
}
