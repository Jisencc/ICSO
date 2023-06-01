package com.demo.icso.Bean;

import java.io.Serializable;

/**
 * created by CJS on 2021/5/7 15:41
 *
 */
public class Calibration implements Serializable {
    private int Red;
    private int Blue;
    private int Green;
    private double Concentration;
    private String Unit_of_measure;

    public int getRed() {
        return Red;
    }

    public void setRed(int red) {
        Red = red;
    }

    public int getBlue() {
        return Blue;
    }

    public void setBlue(int blue) {
        Blue = blue;
    }

    public int getGreen() {
        return Green;
    }

    public void setGreen(int green) {
        Green = green;
    }

    public double getConcentration() {
        return Concentration;
    }

    public void setConcentration(double concentration) {
        Concentration = concentration;
    }

    public String getUnit_of_measure() {
        return Unit_of_measure;
    }

    public void setUnit_of_measure(String unit_of_measure) {
        Unit_of_measure = unit_of_measure;
    }

    public Calibration() {

    }

    public Calibration(int red, int blue, int green, double concentration, String unit_of_measure) {
        Red = red;
        Blue = blue;
        Green = green;
        Concentration = concentration;
        Unit_of_measure = unit_of_measure;
    }



}
