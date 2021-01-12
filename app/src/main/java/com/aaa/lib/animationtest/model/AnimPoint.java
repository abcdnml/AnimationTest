package com.aaa.lib.animationtest.model;

import android.graphics.Color;
import android.graphics.PointF;

import androidx.core.util.Pools;

import java.util.Random;

public class AnimPoint {
    public static final int maxPointCount = 100;   //逃逸速度
    public static int poolCurrentCount=0;
    private final int escapeDistance = 10;   //逃逸速度

    //初始化对象池
    private static final Pools.SynchronizedPool<AnimPoint> sPool =new Pools.SynchronizedPool<>(maxPointCount);

    public float x;
    public float y;
    public float radiu;
    public float speed;
    public float direction;
    public int color;
    public int life;
    public int maxLife;
    public boolean isCatched;
    public int touchLineAlpha;

    private AnimPoint() {
    }

    public void next(float max_w, float max_h, PointF touchPoint, float catchLength, float touchOffX, float touchOffY) {
        float tempX = x + (float) Math.cos(direction * 2 * Math.PI / 360) * speed;
        float tempY = y + (float) Math.sin(direction * 2 * Math.PI / 360) * speed;
        if (tempX >= max_w) {
            tempX = max_w;
            direction = 180 - direction;
        } else if (tempX <= 0) {
            tempX = 0;
            direction = 180 - direction;
        }
        if (tempY >= max_h) {
            tempY = max_h;
            direction = 360 - direction;
        } else if (tempY <= 0) {
            tempY = 0;
            direction = 360 - direction;
        }
        if (touchPoint == null) {
            x = tempX;
            y = tempY;
            isCatched = false;
        } else {
            double distance = distance(tempX, tempY, touchPoint.x, touchPoint.y);
            if (isCatched) {
                if (distance < catchLength) {
                    x = tempX;
                    y = tempY;
                } else {
                    if (touchOffX > escapeDistance || touchOffY > escapeDistance) {
                        //逃逸
                        isCatched = false;
                    } else {
                        x += touchOffX;
                        y += touchOffY;
                    }
                }
                touchLineAlpha = 255 - (int) (distance / catchLength * 255);
            } else {
                // 是否捕获
                if (distance <= catchLength) {
                    isCatched = true;
                    touchLineAlpha = 255 - (int) (distance / catchLength * 255);
                }
                x = tempX;
                y = tempY;
            }
        }

        life++;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public boolean isDead() {
        if (life > maxLife) {
            return true;
        } else {
            return false;
        }
    }

    private void initPoint() {
        Random random = new Random();
        int a = random.nextInt(155) + 100;
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        x = random.nextFloat() * 1000;
        y = random.nextFloat() * 1900;

        radiu = random.nextInt(6)+6;
        color = Color.argb(a, r, g, b);
        touchLineAlpha = 0;

        speed = random.nextFloat() * 2 + 1;
        direction = random.nextFloat() * 360;

        life = 0;
        maxLife = random.nextInt(2000) + 500;

        isCatched = false;
    }

    /**
     * 重置对象状态
     */
    private void reset() {
        x = 0;
        y = 0;

        radiu = 0;
        color = 0;
        touchLineAlpha = 0;

        speed = 0;
        direction = 0;

        life = 0;
        maxLife = 0;

        isCatched = false;
    }

    /**
     * 回收对象：初始化对象-->存入对象池
     */
    public void recycle() {
        this.reset();
        poolCurrentCount++;
        sPool.release(this);
    }


    /**
     * 获取（创建对象）
     * 默认从对象池中获取，拿不到就new
     *
     * @return AnimPoint
     */
    public static AnimPoint obtain() {
        AnimPoint instance = sPool.acquire();
        if(instance==null){
            instance=new AnimPoint();
        }else{
            poolCurrentCount--;
        }
        instance.initPoint();
        return instance;
    }

}
