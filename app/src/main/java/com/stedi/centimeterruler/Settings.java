package com.stedi.centimeterruler;

import android.content.SharedPreferences;

public class Settings {
    private static Settings instance;

    private static final String KEY_CALIBRATION = "calibration_value";
    private static final String KEY_THEME = "ruler_theme";

    public enum Theme {
        BLUE(R.color.blue, R.color.blue_darker, R.style.DialogBlueTheme),
        GREEN(R.color.green, R.color.green_darker, R.style.DialogGreenTheme),
        YELLOW(R.color.yellow, R.color.yellow_darker, R.style.DialogYellowTheme),
        GRAY(R.color.gray, R.color.gray_darker, R.style.DialogGrayTheme);

        public final int rulerColor;
        public final int elementsColor;
        public final int dialogThemeResId;

        Theme(int rulerColorResId, int elementsColorResId, int dialogThemeResId) {
            this.rulerColor = App.color(rulerColorResId);
            this.elementsColor = App.color(elementsColorResId);
            this.dialogThemeResId = dialogThemeResId;
        }

        public static Theme find(String name) {
            for (Theme theme : Theme.values()) {
                if (theme.name().equals(name))
                    return theme;
            }
            return Constants.DEFAULT_THEME;
        }
    }

    private int lastCalibration;
    private int calibration;
    private Theme lastTheme;
    private Theme theme;

    public static Settings getInstance() {
        if (instance == null)
            instance = new Settings();
        return instance;
    }

    private Settings() {
        SharedPreferences sp = App.getSharedPreferences();
        lastCalibration = sp.getInt(KEY_CALIBRATION, Constants.DEFAULT_CALIBRATION);
        lastTheme = Theme.find(sp.getString(KEY_THEME, Constants.DEFAULT_THEME.name()));
        calibration = lastCalibration;
        theme = lastTheme;
    }

    public int getCalibration() {
        return calibration;
    }

    public void setCalibration(int calibration) {
        if (this.calibration == calibration)
            return;
        this.calibration = calibration;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        if (this.theme == theme)
            return;
        this.theme = theme;
    }

    public boolean isChanged() {
        return lastCalibration != calibration || lastTheme != theme;
    }

    public void revert() {
        calibration = lastCalibration;
        theme = lastTheme;
    }

    public void commit() {
        App.saveSharedPreferences(editor -> {
            lastCalibration = calibration;
            lastTheme = theme;
            editor.putInt(KEY_CALIBRATION, calibration);
            editor.putString(KEY_THEME, theme.name());
        });
    }
}
