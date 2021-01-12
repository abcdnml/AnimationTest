package com.aaa.lib.animationtest.model;

import java.lang.Math;

public class AnimPoint {
    public float x;
    public float y;
    public float radiu;
    public float speed;
    public float direction;
    public int color;
    public int life;

    public AnimPoint(float x, float y, float radiu,float speed, float direction, int color) {
        this.x = x;
        this.y = y;
        this.radiu=radiu;
        this.speed = speed;
        this.direction = direction;
        this.color = color;
    }
    public void next(float max_w,float max_h) {
        x = x + Math.cos(speed);
        y = y + Math.sin(speed);
        boolean turnBack = false;
        if (x >= max_w) {
            x = max_w;
            turnBack = true;
        }
        if (x <= 0) {
            x = 0;
            turnBack = true;
        }
        if (y >= max_h) {
            y = max_h;
            turnBack = true;
        }
        if (y <= 0) {
            y = 0;
            turnBack = true;
        }
        if (turnBack) {
            direction = 180 - direction;
        }
        life++;
    }
}
