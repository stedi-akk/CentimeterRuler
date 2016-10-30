package com.stedi.centimeterruler.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.stedi.centimeterruler.App;
import com.stedi.centimeterruler.R;

public class CalibrationBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    private ShapeDrawable thumbDrawable;
    private GradientDrawable backgroundDrawable;

    private OnChange cachedOnChange;

    public static class OnChange {
        public int value;
    }

    public CalibrationBar(Context context) {
        this(context, null);
    }

    public CalibrationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalibrationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.calibrationbar_background); // can't create it programmatically
        backgroundDrawable = (GradientDrawable) ((LayerDrawable) getBackground()).getDrawable(0);
        thumbDrawable = createThumbDrawable();
        setThumb(thumbDrawable);
        setProgressDrawable(null);
        setThumbOffset(0);
        setOnSeekBarChangeListener(this);
    }

    public void setColor(int color) {
        backgroundDrawable.setColor(color);
        thumbDrawable.getPaint().setColor(color);
    }

    public void setCalibrationProgress(int progress) {
        setOnSeekBarChangeListener(null);
        setProgress(progress);
        if (getWidth() > 0 && getHeight() > 0)
            onSizeChanged(getWidth(), getHeight(), 0, 0);
        setOnSeekBarChangeListener(this);
    }

    public int getCalibrationProgress() {
        return getProgress();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (cachedOnChange == null)
            cachedOnChange = new OnChange();
        else if (cachedOnChange.value == progress)
            return;
        cachedOnChange.value = progress;
        App.getBus().post(cachedOnChange);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private ShapeDrawable createThumbDrawable() {
        int shapeSize = (int) App.dp2px(24);
        ShapeDrawable shape = new ShapeDrawable(new OvalShape());
        shape.getPaint().setColor(Color.BLACK);
        shape.setIntrinsicHeight(shapeSize);
        shape.setIntrinsicWidth(shapeSize);
        return shape;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
