package com.stedi.centimeterruler.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.stedi.centimeterruler.App;
import com.stedi.centimeterruler.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPicker extends LinearLayout {
    private final float ovalRadius = App.dp2px(36);
    private final float selectedStrokeWidth = App.dp2px(3);

    private List<Picker> items = new ArrayList<>();

    public static class OnSelected {
        public final int index;

        public OnSelected(int index) {
            this.index = index;
        }
    }

    private class Picker {
        private final View view;
        private final int fillColor;
        private final int strokeColor;

        private Picker(View view, int fillColor, int strokeColor) {
            this.view = view;
            this.fillColor = fillColor;
            this.strokeColor = strokeColor;
        }
    }

    public ColorPicker(Context context) {
        super(context);
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addPicker(int fillColor, int strokeColor) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View pickerView = inflater.inflate(R.layout.color_picker_item, this, false);
        addView(pickerView);
        Picker picker = new Picker(pickerView, fillColor, strokeColor);
        items.add(picker);
        pickerView.setOnClickListener(v -> {
            int index = items.indexOf(picker);
            setSelected(index);
            App.getBus().post(new OnSelected(index));
        });
        refreshPicker(picker, false);
    }

    public void setSelected(int index) {
        for (int i = 0; i < items.size(); i++) {
            Picker picker = items.get(i);
            refreshPicker(picker, i == index);
        }
    }

    private void refreshPicker(Picker picker, boolean selected) {
        Drawable drawable = createOvalShape(selected, picker.fillColor, picker.strokeColor);
        picker.view.setBackgroundDrawable(drawable);
    }

    private Drawable createOvalShape(boolean selected, int fillColor, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(ovalRadius);
        if (selected)
            drawable.setStroke((int) selectedStrokeWidth, strokeColor);
        return drawable;
    }
}
