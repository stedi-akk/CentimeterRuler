package com.stedi.centimeterruler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

// TODO toast on change calibration
// TODO resize full ruler on calibration
// TODO better ruler and calibration menu design
// TODO correct calibration method
// TODO move ruler in any direction (multi touch)
public class MainActivity extends Activity implements OnTouchListener, OnSeekBarChangeListener {
    private static final String CALIBRATION_VALUE = "calibration_value";

    private LinearLayout settingsLayout;
    private SeekBar sbCalibration;
    private Ruler ruler;
    private RelativeLayout.LayoutParams rulerParams;
    private SharedPreferences preferences;

    private int screenWidth;
    private int xPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        preferences = getPreferences(MODE_PRIVATE);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        settingsLayout = (LinearLayout) findViewById(R.id.settings_layout);
        sbCalibration = (SeekBar) findViewById(R.id.sb_calibration);
        sbCalibration.setOnSeekBarChangeListener(this);
        ruler = (Ruler) findViewById(R.id.ruler);
        ruler.setOnTouchListener(this);
        ruler.calibrate(loadCalibration(), sbCalibration.getMax());
        rulerParams = (RelativeLayout.LayoutParams) ruler.getLayoutParams();
        rulerParams.leftMargin = screenWidth / 2;
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
                else if (xMove + ruler.getWidth() >= screenWidth)
                    rulerParams.leftMargin = screenWidth - ruler.getWidth();
                else
                    rulerParams.leftMargin = xMove;
                ruler.setLayoutParams(rulerParams);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ruler.calibrate(progress, sbCalibration.getMax());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public void calibration(View v) {
        if (sbCalibration.getVisibility() == View.GONE) {
            sbCalibration.setVisibility(View.VISIBLE);
            settingsLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.settings_show));
            settingsLayout.bringToFront();
            sbCalibration.setProgress(loadCalibration());
        } else {
            Animation settingsHide = AnimationUtils.loadAnimation(this, R.anim.settings_hide);
            settingsHide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    sbCalibration.setVisibility(View.GONE);
                    ruler.bringToFront();
                    saveCalibration();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            settingsLayout.startAnimation(settingsHide);
        }
    }

    private void saveCalibration() {
        Editor editor = preferences.edit();
        editor.putInt(CALIBRATION_VALUE, sbCalibration.getProgress());
        editor.commit();
    }

    private int loadCalibration() {
        return preferences.getInt(CALIBRATION_VALUE, 20);
    }
}
