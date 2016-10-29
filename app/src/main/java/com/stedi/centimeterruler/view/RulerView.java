package com.stedi.centimeterruler.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.stedi.centimeterruler.Constants;
import com.stedi.centimeterruler.Settings;

public class RulerView extends View implements View.OnTouchListener {
    private Paint linePaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private float mm;
    private float sm;
    private float textMargin;
    private float calibration;

    private int xPositionTouch;
    private int drawX;

    private final int RULER_WIDTH = (int) getTypedValue(TypedValue.COMPLEX_UNIT_MM, 20);

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(getTypedValue(TypedValue.COMPLEX_UNIT_DIP, 1));
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(getTypedValue(TypedValue.COMPLEX_UNIT_MM, 4));
        textMargin = getTypedValue(TypedValue.COMPLEX_UNIT_MM, 1);
        mm = getTypedValue(TypedValue.COMPLEX_UNIT_MM, 1);
        sm = getTypedValue(TypedValue.COMPLEX_UNIT_MM, 10);
        setOnTouchListener(this);
        setBackgroundColor(Settings.getInstance().getTheme().rulerColor);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xPositionTouch = x - drawX;
                break;
            case MotionEvent.ACTION_MOVE:
                int xMove = x - xPositionTouch;
                if (xMove <= 0)
                    drawX = 0;
                else if (xMove + RULER_WIDTH >= getWidth())
                    drawX = getWidth() - RULER_WIDTH;
                else
                    drawX = xMove;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRulerBackground(canvas);
        drawRulerValues(canvas);
    }

    private void drawRulerBackground(Canvas canvas) {
        canvas.drawRect(drawX, 0, drawX + RULER_WIDTH, getBottom(), backgroundPaint);
    }

    private void drawRulerValues(Canvas canvas) {
        float mmY = 0;
        float smY = mm * 3;
        int smText = 0;
        while (smY <= getMeasuredHeight() - mm * 3) {
            canvas.drawLine(drawX, smY, drawX + RULER_WIDTH / 2, smY, linePaint);
            canvas.save();
            canvas.rotate(90, RULER_WIDTH / 2 + textMargin, smY - textMargin);
            canvas.drawText(smText + "", RULER_WIDTH / 2 + textMargin, smY - textMargin - drawX, textPaint);
            canvas.restore();
            mmY = smY;
            for (int i = 0; i < 9; i++) {
                mmY += (mm + calibration);
                if (mmY > getMeasuredHeight() - mm * 3)
                    break;
                if (i == 4)
                    canvas.drawLine(drawX, mmY, drawX + RULER_WIDTH / 3, mmY, linePaint);
                else
                    canvas.drawLine(drawX, mmY, drawX + RULER_WIDTH / 4, mmY, linePaint);
            }
            smY += (sm + calibration * 10);
            smText++;
        }
    }

    public void calibrate(float value) {
        calibration = (Constants.MAX_CALIBRATION - value - (Constants.MAX_CALIBRATION / 2)) / 12.5f;
        invalidate();
    }

    private float getTypedValue(int unit, float value) {
        return TypedValue.applyDimension(unit, value, getResources().getDisplayMetrics());
    }
}
