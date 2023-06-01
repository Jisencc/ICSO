package com.demo.icso.Bean;

import java.io.Serializable;

/**
 * created by CJS on 2021/5/7 19:17
 */
public class PointD implements Serializable {

    public double x = 0d;
    public double y = 0d;

    public PointD()
    {
    }

    public PointD(double x,double y)
    {
        this.x = x;
        this.y = y;
    }

}
