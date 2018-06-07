package com.smarteist.autoimageslider.IndicatorView.animation.data.type;


import com.smarteist.autoimageslider.IndicatorView.animation.data.Value;

public class DropAnimationValue implements Value {

    private int width;
    private int height;
    private int radius;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
