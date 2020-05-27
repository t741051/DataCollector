package com.mitlab.zusliu.User.Interface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PaintBoard extends View {

    public PaintBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
/*
        //paint a circle

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawCircle(120, 80, 60, paint);

 */

        //paint string

        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);
        //canvas.drawText("My name is Linc!",245,140,paint);

        //draw line

        paint.setColor(Color.GREEN);
        //canvas.drawLine(245,145,500,145,paint);
    }

}