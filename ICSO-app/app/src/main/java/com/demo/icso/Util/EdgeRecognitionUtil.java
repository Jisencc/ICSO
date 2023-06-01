package com.demo.icso.Util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.demo.icso.Bean.PixelXY;

import java.math.BigInteger;

/**
 * created by CJS on 2021/4/30 12:45
 */
public class EdgeRecognitionUtil {
    private static int width;
    private static int height;
    private static int add_color = 0;
    private static int num;
    private static int red, green, blue;
    public static int avg_red = 0;
    public static int avg_green = 0;
    public static int avg_blue = 0;
    public static boolean flag = false;
    public static double lastGrad = 0;
    public static int edgeMinX = 0;
    public static int edgeMinY = 0;
    public static int edgeMaxY = 0;
    public static int edgeMaxX = 0;
    public static boolean endFlag = false;
    public static int edgeWidth = 0;

    /**
     * @param bitmapl
     * @return
     * @author CJS
     * @time 2021/5/1  11:13
     * @describe
     */
    public static Bitmap SobelOperator(Bitmap bitmapl) {
        Bitmap bitmap = GrayscaleUtil.compress(bitmapl, 300, 300);
        PixelXY pixelXY = new PixelXY();
        Bitmap grayBitmap = GrayscaleUtil.toGrayscale(bitmap);
        width = grayBitmap.getWidth();
        height = grayBitmap.getHeight();
        int[] prim_colorArray = new int[width * height];
        bitmap.getPixels(prim_colorArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int[] colorArray = new int[width * height];
        double[] gradArray = new double[width * height];
        int[] resultArray = new int[width * height];
        grayBitmap.getPixels(colorArray, 0, grayBitmap.getWidth(), 0, 0, grayBitmap.getWidth(), grayBitmap.getHeight());
        double max = Double.MIN_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                gradArray[i + j * width] = Math.sqrt(gradX(i, j, grayBitmap) * gradX(i, j, grayBitmap) + gradY(i, j, grayBitmap) * gradY(i, j, grayBitmap));
                if (gradArray[i + j * width] > max) {
                    max = gradArray[i + j * width];
                }
            }
        }
        double top = max*0.06;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                if (gradArray[i + j * width] > top) {
//                    resultArray[i + j * width] = Color.RED;
                    resultArray[i + j * width] = Color.YELLOW;
                } else {
//                    resultArray[i + j * width] = colorArray[i + j * width];
                    resultArray[i + j * width] = prim_colorArray[i + j * width];
                }

            }
        Bitmap bitmap1 = Bitmap.createBitmap(resultArray, grayBitmap.getWidth(), grayBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        catchEdge(bitmap1);
        return bitmap1;


    }

    /**
     * @param bitmap
     * @author CJS
     * @time 2021/5/6  11:41
     * @describe
     */
    public static void catchEdge(Bitmap bitmap) {
        int i = bitmap.getWidth() / 2;
        int j = bitmap.getHeight() / 2;
        while (i > 0) {
//            if (bitmap.getPixel(i, j) == Color.RED)
            if (bitmap.getPixel(i, j) == Color.YELLOW) {
                edgeMinX = ++i;
                Log.d("MIN", "catchEdge: " + edgeMinX);
                break;
            }
            i--;
        }
        i = bitmap.getWidth() / 2;
        j = bitmap.getHeight() / 2;
        while (j > 0) {
//            if (bitmap.getPixel(i, j) == Color.RED)
            if (bitmap.getPixel(i, j) == Color.YELLOW) {
                edgeMinY = ++j;
                Log.d("MIN", "catchEdge: " + edgeMinY);
                break;
            }
            j--;
        }
        i = bitmap.getWidth() / 2;
        j = bitmap.getHeight() / 2;
        while (i < bitmap.getWidth()) {
//            if (bitmap.getPixel(i, j) == Color.RED)
            if (bitmap.getPixel(i, j) == Color.YELLOW) {
                edgeMaxX = --i;
                Log.d("MAX", "catchEdge: " + edgeMaxX);
                break;
            }
            i++;
        }
        i = bitmap.getWidth() / 2;
        j = bitmap.getHeight() / 2;
        while (j < bitmap.getHeight()) {
//            if (bitmap.getPixel(i, j) == Color.RED)
            if (bitmap.getPixel(i, j) == Color.YELLOW) {
                edgeMaxY = --j;
                Log.d("MAX", "catchEdge: " + edgeMaxY);
                break;
            }
            j++;
        }
        avg_RGB(bitmap);
    }

    /**
     * @param bitmap
     * @author CJS
     * Catch RGB
     */
    public static void avg_RGB(Bitmap bitmap) {
        red = 0;
        green = 0;
        blue = 0;
        num = 0;
        for (int i = edgeMinX + 5; i < edgeMaxX - 5; i++)
            for (int j = edgeMinY + 5; j < edgeMaxY - 5; j++) {
                red += Color.red(bitmap.getPixel(i, j));
                green += Color.green(bitmap.getPixel(i, j));
                blue += Color.blue(bitmap.getPixel(i, j));
                num++;
            }
        try {
            avg_red = red / num;
            avg_green = green / num;
            avg_blue = blue / num;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static int avgColor() {
        return add_color / num;
    }

    /**
     * @param
     * @author CJS
     * @time 2021/5/1  13:15
     * @describe Calculating Grad
     */
    public static double gradX(int x, int y, Bitmap bitmap) {
        double gradLine1 = (-1) * getPixel(x - 1, y - 1, bitmap) + getPixel(x - 1, y + 1, bitmap);
        double gradLine2 = (-2) * getPixel(x, y - 1, bitmap) + 2 * getPixel(x, y + 1, bitmap);
        double gradLine3 = (-1) * getPixel(x + 1, y - 1, bitmap) + getPixel(x + 1, y + 1, bitmap);
        double gradX = gradLine1 + gradLine2 + gradLine3;
        return gradX;
    }

    /**
     * @param
     * @author CJS
     * @time 2021/5/1  13:18
     * @describe
     */
    public static double gradY(int x, int y, Bitmap bitmap) {
        double gradColumn1 = (-1) * getPixel(x + 1, y - 1, bitmap) + getPixel(x - 1, y - 1, bitmap);
        double gradColumn2 = (-2) * getPixel(x + 1, y, bitmap) + 2 * getPixel(x - 1, y, bitmap);
        double gradColumn3 = (-1) * getPixel(x + 1, y + 1, bitmap) + getPixel(x - 1, y + 1, bitmap);
        double gradY = gradColumn1 + gradColumn2 + gradColumn3;
        return gradY;
    }

    public static double getPixel(int x, int y, Bitmap bitmap) {
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight())
            return 0;
        else
            return bitmap.getPixel(x, y);
    }
}
