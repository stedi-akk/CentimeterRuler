package com.stedi.centimeterruler;

import android.content.SharedPreferences;
import android.graphics.Color;

public class Settings {
    private static Settings instance;

    private static final String KEY_CALIBRATION = "calibration_value";
    private static final String KEY_RULER_COLOR = "ruler_color";

    public enum RulerColor {
        BLUE("#9ecaff", "#6baeff"),
        GREEN("#aeff9e", "#59e43d"),
        YELLOW("#f6ff9e", "#e1ed68"),
        GRAY("#e3e3e3", "#c6c6c6");

        public final int color;
        public final int darkerColor;

        RulerColor(String colorHex, String darkerColorHex) {
            this.color = Color.parseColor(colorHex);
            this.darkerColor = Color.parseColor(darkerColorHex);
        }

        public static RulerColor find(String name) {
            for (RulerColor rulerColor : RulerColor.values()) {
                if (rulerColor.name().equals(name))
                    return rulerColor;
            }
            return Constants.DEFAULT_RULER_COLOR;
        }
    }

    private int calibration;
    private RulerColor rulerColor;

    public static Settings getInstance() {
        if (instance == null)
            instance = new Settings();
        return instance;
    }

    private Settings() {
        SharedPreferences sp = App.getSharedPreferences();
        calibration = sp.getInt(KEY_CALIBRATION, Constants.DEFAULT_CALIBRATION);
        rulerColor = RulerColor.find(sp.getString(KEY_RULER_COLOR, Constants.DEFAULT_RULER_COLOR.name()));
    }

    public int getCalibration() {
        return calibration;
    }

    public void setCalibration(int calibration) {
        if (this.calibration == calibration)
            return;
        this.calibration = calibration;
    }

    public RulerColor getRulerColor() {
        return rulerColor;
    }

    public void setRulerColor(RulerColor rulerColor) {
        if (this.rulerColor == rulerColor)
            return;
        this.rulerColor = rulerColor;
    }

    public void save() {
        App.saveSharedPreferences(editor -> {
            editor.putInt(KEY_CALIBRATION, calibration);
            editor.putString(KEY_RULER_COLOR, rulerColor.name());
        });
    }
}
