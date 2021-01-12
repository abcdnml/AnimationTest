package com.aaa.lib.animationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.aaa.lib.animationtest.model.AnimPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoNameAnimView extends View {
    private int maxPointCount = 1000;   //最大点的数量
    private int maxLife = 100;      //点的生命上限， 每一次刷新增加1点 到最大值时 消失;
    private int duration = 100;     //动画刷新间隔ms
    private volatile boolean show = false;  //是否在显示

    private Thread refreshThread;       //刷新线程
    private List<AnimPoint> animPoints;     // 点集

    private Paint pointPaint;       //画笔 用于绘制点
    private Paint pathPaint;        //画笔 用于绘制线

    public NoNameAnimView(Context context) {
        super(context);
    }

    public NoNameAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoNameAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        refreshThread = new Thread(new RefreshRunnable());
        animPoints = new ArrayList<>();

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPoint(canvas);
        drawLine(canvas);
    }

    private void drawPoint(Canvas canvas) {
        for (AnimPoint animPoint : animPoints) {
            // 更新point状态
            animPoint.next(getWidth(),getHeight());
            // draw point
            pointPaint.setColor(animPoint.color);
            canvas.drawCircle(animPoint.x, animPoint.y, animPoint.radiu, pointPaint);
        }

    }

    private void drawLine(Canvas canvas) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        show = false;
    }

    private void init() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            float x = random.nextFloat() * 1000;
            float y = random.nextFloat() * 1900;
            float radiu = random.nextInt() * 10;
            float speed = random.nextFloat() * 10 + 3;
            float direction = random.nextFloat() * 360;
            int a = random.nextInt() * 155 + 100;
            int r = random.nextInt() * 255;
            int g = random.nextInt() * 255;
            int b = random.nextInt() * 255;
            int color = Color.argb(a, r, g, b);
            AnimPoint point = new AnimPoint(x, y, radiu, speed, direction, color);
            animPoints.add(point);
        }
        show = true;
        refreshThread.start();
    }

    class RefreshRunnable implements Runnable {

        @Override
        public void run() {
            while (show) {
                try {
                    Thread.sleep(duration);
                    postInvalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
