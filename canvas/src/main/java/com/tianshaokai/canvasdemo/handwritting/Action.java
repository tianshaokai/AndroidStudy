package com.tianshaokai.canvasdemo.handwritting;


import android.graphics.Canvas;
import android.graphics.Color;

/**
 * 基础类
 * Created by tianshaokai on 16-3-12.
 */
public abstract class Action {
    public int color;

    Action() {
        color = Color.BLACK;
    }

    Action(int color) {
        this.color = color;
    }

    public abstract void draw(Canvas canvas);

    public abstract void move(float mx, float my);
}




