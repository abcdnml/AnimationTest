package com.aaa.lib.animationtest.model;

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
}
