package com.stedi.centimeterruler;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.squareup.otto.Bus;

public class App extends Application {
    private static App instance;

    public interface PreferencesEditor {
        void edit(SharedPreferences.Editor editor);
    }

    private Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new Bus();
    }

    public static Bus getBus() {
        return instance.bus;
    }

    public static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(instance);
    }

    public static void saveSharedPreferences(PreferencesEditor editor) {
        SharedPreferences.Editor spEditor = getSharedPreferences().edit();
        editor.edit(spEditor);
        spEditor.apply();
    }

    public static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, instance.getResources().getDisplayMetrics());
    }
}
