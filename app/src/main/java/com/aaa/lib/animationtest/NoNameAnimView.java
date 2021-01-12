package com.aaa.lib.animationtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.aaa.lib.animationtest.model.AnimPoint;

import java.util.ArrayList;
import java.util.List;

public class NoNameAnimView extends View {
    private int maxPointCount = 1000;   //最大点的数量
    private float maxLineLength = 150;   //最大连线距离
    private float catchLength = 300;   //最大捕获半径
    private int maxLife = 100;      //点的生命上限， 每一次刷新增加1点 到最大值时 消失;
    private int duration = 16;     //动画刷新间隔ms
    private int pointBornDuration = 1000;     //动画刷新间隔ms
    private volatile boolean show = false;  //是否在显示

    private Thread refreshThread;       //刷新线程
    private Thread pointBornThread;       //生成点的线程
    private List<AnimPoint> animPoints;     // 点集
    private List<AnimPoint> survivePoints;     // 存活的点集

    private PointF touchPoint;

    private Paint pointPaint;       //画笔 用于绘制点
    private Paint pathPaint;        //画笔 用于绘制线

    private float offsetX;
    private float offsetY;


    public NoNameAnimView(Context context) {
        super(context);
        init();
    }

    public NoNameAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoNameAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        refreshThread = new Thread(new RefreshRunnable());
        pointBornThread=new Thread(new PointBornRunnable());

        animPoints = new ArrayList<>();
        survivePoints = new ArrayList<>();

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

        removeDead();
        drawPoint(canvas);
        drawLine(canvas);
    }

    /**
     *  移除老死的点 添加新点
     */
    private void removeDead(){
        survivePoints.clear();
        for(AnimPoint animPoint:animPoints){
            if(!animPoint.isDead()){
                survivePoints.add(animPoint);
            }
        }
        animPoints.clear();
        animPoints.addAll(survivePoints);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchPoint = new PointF(touchX, touchY);
            offsetX = 0;
            offsetY = 0;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            offsetX += touchX - touchPoint.x;
            offsetY += touchY - touchPoint.y;
            touchPoint.set(touchX, touchY);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            touchPoint = null;
            offsetX = 0;
            offsetY = 0;
        }
        return true;
    }

    private void drawPoint(Canvas canvas) {
        for (AnimPoint animPoint : animPoints) {
            // 更新point状态
            animPoint.next(getWidth(), getHeight(), touchPoint, catchLength, offsetX, offsetY);
            // draw point
            pointPaint.setColor(animPoint.color);
            canvas.drawCircle(animPoint.x, animPoint.y, 6, pointPaint);
        }
        offsetY = 0;
        offsetX = 0;
    }

    private void drawLine(Canvas canvas) {
        for (int i = 0; i < animPoints.size(); i++) {
            AnimPoint pi = animPoints.get(i);
            for (int j = i + 1; j < animPoints.size(); j++) {
                AnimPoint pj = animPoints.get(j);
                double distance = distance(pi, pj);
                if (distance <= maxLineLength) {
                    int color = Color.argb(255 - (int) (distance / maxLineLength * 255), 0, 0, 0);
                    pathPaint.setColor(color);
                    canvas.drawLine(pi.x, pi.y, pj.x, pj.y, pathPaint);
                }
            }
            if (pi.isCatched) {
                int color = Color.argb(pi.touchLineAlpha, 0, 0, 0);
                pathPaint.setColor(color);
                canvas.drawLine(pi.x, pi.y, touchPoint.x, touchPoint.y, pathPaint);
            }
        }
    }

    public double distance(AnimPoint p1, AnimPoint p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initPoint();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        show = false;
    }

    private void initPoint() {
        for (int i = 0; i < 100; i++) {
            AnimPoint point = new AnimPoint();
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
    class PointBornRunnable implements Runnable {
        @Override
        public void run() {
            while (show) {
                try {
                    Thread.sleep(pointBornDuration);

                    postInvalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
