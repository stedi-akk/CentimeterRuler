package com.stedi.centimeterruler.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.stedi.centimeterruler.R;
import com.stedi.centimeterruler.Settings;

public class RulerView extends View {
    private Paint linePaint;
    private Paint textPaint;

    private float mm;
    private float sm;
    private float textMargin;
    private float calibration;

    public RulerView(Context context) {
        super(context);
        init();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getResources().getDisplayMetrics().heightPixels;
        int measuredWidth = (int) getTypedValue(TypedValue.COMPLEX_UNIT_MM, 20);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mmY = 0;
        float smY = mm * 3;
        int smText = 0;
        while (smY <= getMeasuredHeight() - mm * 3) {
            canvas.drawLine(0, smY, getMeasuredWidth() / 2, smY, linePaint);
            canvas.save();
            canvas.rotate(90, getMeasuredWidth() / 2 + textMargin, smY - textMargin);
            canvas.drawText(smText + "", getMeasuredWidth() / 2 + textMargin, smY - textMargin, textPaint);
            canvas.restore();
            mmY = smY;
            for (int i = 0; i < 9; i++) {
                mmY += (mm + calibration);
                if (mmY > getMeasuredHeight() - mm * 3)
                    break;
                if (i == 4)
                    canvas.drawLine(0, mmY, getMeasuredWidth() / 3, mmY, linePaint);
                else
                    canvas.drawLine(0, mmY, getMeasuredWidth() / 4, mmY, linePaint);
            }
            smY += (sm + calibration * 10);
            smText++;
        }
    }

    public void calibrate(float value, int sbMax) {
        calibration = (value - (sbMax / 2)) / 12.5f;
        invalidate();
    }

    private void init() {
        setBackgroundColor(Settings.getInstance().getRulerColor().color);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(getTypedValue(TypedValue.COMPLEX_UNIT_DIP, 1));
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(getTypedValue(TypedValue.COMPLEX_UNIT_MM, 4));
        textMargin = getTypedValue(TypedValue.COMPLEX_UNIT_MM, 1);
        mm = getTypedValue(TypedValue.COMPLEX_UNIT_MM, 1);
        sm = getTypedValue(TypedValue.COMPLEX_UNIT_MM, 10);
    }

    private float getTypedValue(int unit, float value) {
        return TypedValue.applyDimension(unit, value, getResources().getDisplayMetrics());
    }
}
