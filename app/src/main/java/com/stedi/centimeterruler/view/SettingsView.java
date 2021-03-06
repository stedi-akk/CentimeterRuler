package com.stedi.centimeterruler.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.stedi.centimeterruler.App;
import com.stedi.centimeterruler.BuildConfig;
import com.stedi.centimeterruler.Constants;
import com.stedi.centimeterruler.R;
import com.stedi.centimeterruler.Settings;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsView extends FrameLayout {
    private final String KEY_STATE = "KEY_STATE";
    private final String KEY_SHOWED = "KEY_SHOWED";

    public static class OnSettingsChanged {
    }

    @BindViews({R.id.settings_view_tv_calibration, R.id.settings_view_tv_version_info,
            R.id.settings_view_calibration_bar, R.id.settings_view_color_picker})
    List<View> showHideViews;

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

    private boolean showed;

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
        calibrationBar.setMax(Constants.MAX_CALIBRATION);
        for (Settings.Theme theme : Settings.Theme.values())
            colorPicker.addPicker(theme.rulerColor, theme.elementsColor);
        refresh();
        showSettings(false, false);
    }

    @OnClick(R.id.settings_view_btn_show)
    public void onBtnShowClick(View v) {
        if (showed && Settings.getInstance().isChanged()) {
            App.getBus().post(new OnSettingsChanged());
        } else {
            showSettings(!showed, true);
        }
    }

    public void refresh() {
        int calibration = Settings.getInstance().getCalibration();
        if (calibrationBar.getCalibrationProgress() != calibration)
            calibrationBar.setCalibrationProgress(calibration);
        tvCalibration.setText(String.valueOf(calibration));
        Settings.Theme theme = Settings.getInstance().getTheme();
        if (colorPicker.getSelectedIndex() != theme.ordinal())
            colorPicker.setSelected(theme.ordinal());
        calibrationBar.setColor(theme.elementsColor);
        changeButtonColor(theme.elementsColor, false);
    }

    public void showSettings(boolean show, boolean animate) {
        showed = show;
        changeButtonColor(showed ? Settings.getInstance().getTheme().elementsColor : Settings.getInstance().getTheme().rulerColor, animate);
        if (animate) {
            ButterKnife.apply(showHideViews, showed ? SHOW_ANIMATION : HIDE_ANIMATION);
        } else {
            for (View view : showHideViews) {
                view.setVisibility(showed ? View.VISIBLE : View.GONE);
                view.animate().alpha(showed ? 1f : 0f);
            }
        }
    }

    static final ButterKnife.Action<View> SHOW_ANIMATION = (view, index) ->
            view.animate().alpha(1f).setDuration(Constants.ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }
            });

    static final ButterKnife.Action<View> HIDE_ANIMATION = (view, index) ->
            view.animate().alpha(0f).setDuration(Constants.ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(View.GONE);
                }
            });

    private void changeButtonColor(int colorTo, boolean animate) {
        if (animate && btnShow.getTag() != null) {
            int colorFrom = (int) btnShow.getTag();
            ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            anim.setDuration(Constants.ANIM_DURATION);
            anim.addUpdateListener(animator -> btnShow.setColorFilter((int) animator.getAnimatedValue()));
            anim.start();
        } else {
            btnShow.setColorFilter(colorTo);
        }
        btnShow.setTag(colorTo);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            showSettings(bundle.getBoolean(KEY_SHOWED), false);
            state = bundle.getParcelable(KEY_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_STATE, super.onSaveInstanceState());
        bundle.putBoolean(KEY_SHOWED, showed);
        return bundle;
    }
}
