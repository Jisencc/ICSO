package com.demo.icso.Util;



import com.demo.icso.Bean.PointD;

import java.util.ArrayList;
import java.util.List;

/**
 * LinearRegressionUtil
 * author: Jisen Chen
 */
public class LinearRegressionUtil {
    private List<PointD> regressed_data = new ArrayList<>();
    private List<PointD> Axis_data = new ArrayList<>();

    public double getK() {
        return k;
    }

    public double getB() {
        return b;
    }

    private double k = 0d;
    private double b = 0d;
    private double multiple_add = 0d;
    private static double Axis_X = 0d;
    private static double Axis_Y = 0d;
    private double add_x = 0d;
    private double add_y = 0d;
    private double avera_sqare_add_x;
    private double average_multipleXY = 0d;
    private double sqareX_add = 0d;
    private static double average_x = 0d;
    private static double average_y = 0d;
    private double regression_x = 0d;


    public List<PointD> getRegressed_data() {
        return regressed_data;
    }

    /**
     * @param regressed_data
     */
    public void setRegressed_data(List<PointD> regressed_data) {
        this.regressed_data = regressed_data;
    }

    public double easyRegression(final double d) {
        new Runnable() {
            @Override
            public void run() {
                for (PointD d : Axis_data) {

                    Axis_X = d.x;
                    Axis_Y = d.y;
                    multiple_add += d.x * d.y;
                    add_x += d.x;
                    add_y += d.y;
                    sqareX_add += d.x * d.x;

                }
                average_multipleXY = add_y * add_x / Axis_data.size();
                average_x = add_x / Axis_data.size();
                average_y = add_y / Axis_data.size();
                avera_sqare_add_x = (add_x * add_x) / Axis_data.size();

                k = (multiple_add - average_multipleXY) / (sqareX_add - avera_sqare_add_x);
                b = (add_y - (k * add_x)) / Axis_data.size();

                regression_x = (d - b) / k;

            }
        }.run();
        return regression_x;
    }

    public List<PointD> regression(final List<Double> RGB_data_Y) {
        new Runnable() {
            @Override
            public void run() {
                for (PointD d : Axis_data) {

                    Axis_X = d.x;
                    Axis_Y = d.y;
                    multiple_add += d.x * d.y;
                    add_x += d.x;
                    add_y += d.y;
                    sqareX_add += d.x * d.x;

                }
                average_multipleXY = add_y * add_x / Axis_data.size();
                average_x = add_x / Axis_data.size();
                average_y = add_y / Axis_data.size();
                avera_sqare_add_x = (add_x * add_x) / Axis_data.size();
                k = (multiple_add - average_multipleXY) / (sqareX_add - avera_sqare_add_x);
                b = (add_y - (k * add_x)) / Axis_data.size();
                for (Double d : RGB_data_Y) {
                    PointD pointD = new PointD(0d, 0d);
                    regression_x = (d - b) / k;
                    pointD.x = regression_x;
                    pointD.y = d;
                    regressed_data.add(pointD);
                }
            }


        }.run();

        regressed_data.add(new PointD(0,b));
        regressed_data.add(new PointD(100,k*100+b));
        return regressed_data;
    }

    public LinearRegressionUtil() {

    }

    public LinearRegressionUtil(List<PointD> data) {
        Axis_data = data;
    }

    public List<PointD> getAxis_data() {
        return Axis_data;
    }
}
