package com.demo.icso.Bean;

/**
 * created by CJS on 2021/7/23 10:15
 */
public class Scale {
    private int X_start;
    private int Y_start;

    private int tag;
    private int cell_tag = 0;

    public Scale(int x_start, int y_start, int tag, int cell_tag) {
        X_start = x_start;
        Y_start = y_start;
        this.tag = tag;
        this.cell_tag = cell_tag;
    }

    public int getCell_tag() {
        return cell_tag;
    }

    public void setCell_tag(int cell_tag) {
        this.cell_tag = cell_tag;
    }

    public Scale(int x_start, int y_start, int tag) {
        X_start = x_start;
        Y_start = y_start;
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getX_start() {
        return X_start;
    }

    public void setX_start(int x_start) {
        X_start = x_start;
    }

    public int getY_start() {
        return Y_start;
    }

    public void setY_start(int y_start) {
        Y_start = y_start;
    }


}
