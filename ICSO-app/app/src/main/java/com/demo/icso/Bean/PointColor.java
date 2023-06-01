package com.demo.icso.Bean;

/**
 * created by CJS on 2021/5/16 19:21
 */
public class PointColor {
    public double x = 0d;
    public double y_red = 0d;
    public double y_blue = 0d;
    public double y_green = 0d;
    public double y_V = 0d;

    public PointColor(double x, double y_red, double y_blue, double y_green, double y_V) {
        this.x = x;
        this.y_red = y_red;
        this.y_blue = y_blue;
        this.y_green = y_green;
        this.y_V = y_V;
    }

    public PointColor() {
    }


}

