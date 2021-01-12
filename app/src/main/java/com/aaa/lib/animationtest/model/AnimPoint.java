package com.aaa.lib.animationtest.model;

import android.graphics.Color;
import android.graphics.PointF;

import java.util.Random;

public class AnimPoint {
    private final int escapeDistance = 10;   //逃逸速度
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

    public AnimPoint() {
        Random random = new Random();
        x = random.nextFloat() * 1000;
        y = random.nextFloat() * 1900;
        radiu = random.nextInt(10);
        speed = random.nextFloat() * 2 + 1;
        direction = random.nextFloat() * 360;
        maxLife = random.nextInt(2000) + 500;
        int a = random.nextInt(155) + 100;
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        color = Color.argb(a, r, g, b);
    }

    public AnimPoint(float x, float y, float radiu, float speed, float direction, int color) {
        this.x = x;
        this.y = y;
        this.radiu = radiu;
        this.speed = speed;
        this.direction = direction;
        this.color = color;
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
}
