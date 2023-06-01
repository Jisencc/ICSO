package com.demo.icso.Bean;

import java.io.Serializable;

/**
 * created by CJS on 2021/5/10 23:02
 */
public class MyPoint implements Serializable {

    public double x = 0d;
    public double y = 0d;

    public MyPoint()
    {
    }

    public MyPoint(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

}
