package com.demo.icso.Bean;

/**
 * created by CJS on 2021/5/3 13:48
 */
public class PixelXY {
    private  int MAX_X = Integer.MIN_VALUE;
    private  int MAX_Y = Integer.MIN_VALUE;
    private  int MIN_X = Integer.MAX_VALUE;
    private  int MIN_Y = Integer.MAX_VALUE;

    public int getMAX_X() {
        return MAX_X;
    }

    public void setMAX_X(int MAX_X) {
        this.MAX_X = MAX_X;
    }

    public int getMAX_Y() {
        return MAX_Y;
    }

    public void setMAX_Y(int MAX_Y) {
        this.MAX_Y = MAX_Y;
    }

    public int getMIN_X() {
        return MIN_X;
    }

    public void setMIN_X(int MIN_X) {
        this.MIN_X = MIN_X;
    }

    public int getMIN_Y() {
        return MIN_Y;
    }

    public void setMIN_Y(int MIN_Y) {
        this.MIN_Y = MIN_Y;
    }
}
