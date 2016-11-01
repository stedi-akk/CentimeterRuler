package com.stedi.centimeterruler.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.stedi.centimeterruler.App;
import com.stedi.centimeterruler.Constants;

public class RulerView extends View implements View.OnTouchListener {
    private final float MM = App.mm2px(1);
    private final float SM = MM * 10;
    private final float RULER_WIDTH = SM * 2 + MM * 2;
    private final float VALUES_PADDING = MM * 3;
    private final float TEXT_SIZE = MM * 4;

    private Paint backgroundPaint;
    private Paint linesPaint;
    private Paint textPaint;

    private float actionDownX;
    private float drawX;

    private float calibration;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linesPaint.setColor(Color.BLACK);
        linesPaint.setStrokeWidth(App.dp2px(1));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(TEXT_SIZE);
    }

    public void setRulerColor(int color) {
        backgroundPaint.setColor(color);
        invalidate();
    }

    public void calibrate(float value) {
        calibration = (value - (Constants.MAX_CALIBRATION / 2)) / Constants.CALIBRATION_MAGIC;
        invalidate();
    }

    public void setDrawPosition(float x) {
        drawX = x;
    }

    public float getDrawPosition() {
        return drawX;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownX = x - drawX;
                break;
            case MotionEvent.ACTION_MOVE:
                float xMove = x - actionDownX;
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
    protected void onDraw(Canvas canvas) {
        if (drawX < 0)
            drawX = 0;
        if (drawX > getWidth() - RULER_WIDTH)
            drawX = getWidth() - RULER_WIDTH;

        // ruler background
        canvas.drawRect(drawX, 0, drawX + RULER_WIDTH, getBottom(), backgroundPaint);

        // ruler values
        float mmY = 0;
        float smY = getMeasuredHeight() - VALUES_PADDING;
        int i = 0;
        while (smY >= VALUES_PADDING) {
            // sm line
            canvas.drawLine(drawX, smY, drawX + SM, smY, linesPaint);

            // sm text
            canvas.save();
            canvas.rotate(-90, SM, smY);
            String number = String.valueOf(i);
            float textWidth = textPaint.measureText(number);
            canvas.drawText(number, SM - textWidth / 2, smY + TEXT_SIZE + drawX, textPaint);
            canvas.restore();

            // mm lines
            mmY = smY;
            for (int j = 0; j < 9; j++) {
                mmY -= (MM + calibration);
                if (mmY < VALUES_PADDING)
                    break;
                if (j == 4)
                    canvas.drawLine(drawX, mmY, drawX + SM / 1.5f, mmY, linesPaint);
                else
                    canvas.drawLine(drawX, mmY, drawX + SM / 2, mmY, linesPaint);
            }

            smY -= (SM + calibration * 10);
            i++;
        }
    }
}
