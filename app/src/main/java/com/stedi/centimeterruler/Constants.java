package com.stedi.centimeterruler;

public final class Constants {
    public static final float CALIBRATION_MAGIC = 12.5f; // should not be changed for backward compatibility
    public static final int MAX_CALIBRATION = 90;
    public static final int DEFAULT_CALIBRATION = MAX_CALIBRATION / 2;
    public static final Settings.Theme DEFAULT_THEME = Settings.Theme.GRAY;
    public static final int ANIM_DURATION = 200;
    public static final int BTN_SETTINGS_COLOR = App.color(R.color.gray);
}
