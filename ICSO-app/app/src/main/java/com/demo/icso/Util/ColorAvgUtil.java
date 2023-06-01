package com.demo.icso.Util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.demo.icso.Bean.PixelXY;


public class ColorAvgUtil {
    public static double avg_S1 = 0;
    public static double avg_G1 = 0;
    public static double avg_R1 = 0;
    public static double avg_B1 = 0;
    public static double avg_V1 = 0;
    private static int width;
    private static int height;
    private static int add_color = 0;
    private static int num;
    private static int red, green, blue;
    public static int H = 0, S = 0, V = 0;
    private static double S1 = 0;
    private static double V1 = 0;
    public static int avg_red = 0;
    public static int avg_green = 0;
    public static int avg_blue = 0;
    public static int avg_H = 0;
    public static int avg_S = 0;
    public static int avg_V = 0;
    public static boolean flag = false;
    public static double lastGrad = 0;
    public static int edgeMinX = 0;
    public static int edgeMinY = 0;
    public static int edgeMaxY = 0;
    public static int edgeMaxX = 0;
    public static boolean endFlag = false;
    public static int edgeWidth = 0;
    public static int catchAreapointX = 0;
    public static int catchAreapointY = 0;

    /**
     * @param bitmapl
     * @return
     * @author CJS
     * @time 2021/5/17  22:13
     * @describe customized color catching
     */


    public static Bitmap ColorAvgOperator1(Bitmap bitmapl) {
//        Bitmap bitmap = GrayscaleUtil.compress(bitmapl, (bitmapl.getWidth()) * 80 / 100, (bitmapl.getHeight()) * 80 / 100);
        Bitmap bitmap = GrayscaleUtil.compress(bitmapl, 301, 301);
        red = 0;
        green = 0;
        blue = 0;
        num = 0;
        int h = 0, s = 0, v = 0;
        double s1 = 0.0;
        catchColorEdge(bitmap);
        try {

            for (int i = (bitmap.getWidth()) / 2 - 15; i < (bitmap.getWidth()) / 2 + 15; i++) {
                for (int j = (bitmap.getHeight()) / 2 - 15; j < (bitmap.getHeight()) / 2 + 15; j++) {
                    {
                        {
                            h += H;
                            s += S;
                            s1 += S1;
                            v += V;
                            red += Color.red(bitmap.getPixel(i, j));
                            green += Color.green(bitmap.getPixel(i, j));
                            blue += Color.blue(bitmap.getPixel(i, j));
                            num++;
                        }
                    }
                }
            }

            avg_green = green / num;
            avg_G1 = green / (double) num;
            avg_R1 = red / (double) num;
            avg_B1 = blue / (double) num;
            if ((avg_G1 * 10) % 10 >= 5) {
                avg_green = (int) (avg_green + 1);

            }
            avg_red = red / num;

            avg_blue = blue / num;
            toHSV(avg_red, avg_green, avg_blue);
            avg_H = H;
            avg_S = S;
            avg_V = V;
            avg_S1 = S1;

            avg_V1 = V1;


        } catch (Exception e) {
            e.printStackTrace();
        }
//        Bitmap bitmap1 = Bitmap.createBitmap(resultArray, grayBitmap.getWidth(), grayBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        return bitmap;


    }

    public static String ColorAvgOperatorQuick(Bitmap bitmap, int x, int y, int position) {
        red = 0;
        green = 0;
        blue = 0;
        num = 0;
        int h = 0, s = 0, v = 0;
        double s1 = 0.0;
        catchColorEdge(bitmap);
        try {
            for (int i = x / 2 - 20; i < x / 2 + 20; i++) {
                for (int j = y / 2 - 20; j < y / 2 + 20; j++) {
                    if (!(Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100) && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 100 && Color.green(bitmap.getPixel(i, j)) > 100)) {
                        h += H;
                        s += S;
                        s1 += S1;
                        v += V;
                        red += Color.red(bitmap.getPixel(i, j));
                        green += Color.green(bitmap.getPixel(i, j));
                        blue += Color.blue(bitmap.getPixel(i, j));
                        num++;
                    }
//
                }
//
            }

            avg_red = red / num;
            avg_green = green / num;
            avg_blue = blue / num;
            toHSV(avg_red, avg_green, avg_blue);
            avg_H = H;
            avg_S = S;
            avg_V = V;
            avg_S1 = S1;


        } catch (Exception e) {
            e.printStackTrace();
        }
//        Bitmap bitmap1 = Bitmap.createBitmap(resultArray, grayBitmap.getWidth(), grayBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cell:" + position + "R：" + ColorAvgUtil.avg_red + " G :" + ColorAvgUtil.avg_green + " B :" + ColorAvgUtil.avg_blue + " H :" + ColorAvgUtil.avg_H + " S :" + ColorAvgUtil.avg_S + " V :" + ColorAvgUtil.avg_V + " X:" + x + " Y:" + y);
        return stringBuilder.toString();


    }


    /**
     * @param
     * @author CJS
     * @time 2021/5/19  17:47
     * @describe The method filters color interference
     */
//    public static Bitmap ColorPercentageOperator(Bitmap bitmapl) {
//        Bitmap bitmap = GrayscaleUtil.compress(bitmapl, 300, 300);
//        int max_color = 0;
//        int min_color = 256;
//        int max_X=0, max_Y=0, min_X=0, min_Y=0;
//        red = 0;
//        green = 0;
//        blue = 0;
//        num = 0;
//        for (int i = 0; i < bitmap.getWidth(); i++) {
//            for (int j = 0; j < bitmap.getHeight(); j++) {
//                if (!(Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100) && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200)
//                        &&!(Color.red(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200)&&!(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 200 )
//                        ) {
//                if (!(Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100) && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200)) {
//                    if (Color.red(bitmap.getPixel(i, j)) > max_color) {
//                        max_color = Color.red(bitmap.getPixel(i, j));
//                        max_X = i;
//                        max_Y = j;
//                    }
//                    if (Color.red(bitmap.getPixel(i, j)) < min_color) {
//                        min_color = Color.red(bitmap.getPixel(i, j));
//                        min_X = i;
//                        min_Y = j;
//                    }
//                }
//
//            }
//
//        }
//        Log.d("Max", "" + max_color);
//
//        for (int i = 0; i < bitmap.getWidth(); i++) {
//            for (int j = 0; j < bitmap.getHeight(); j++) {
//                if (!(Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100) && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200)
//                        && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200) && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 200)
//                        && !(Color.blue(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200)) {
//                    if (!(Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100) && !(Color.red(bitmap.getPixel(i, j)) > 200 && Color.blue(bitmap.getPixel(i, j)) > 200 && Color.green(bitmap.getPixel(i, j)) > 200)) {
//                        if (Color.red(bitmap.getPixel(i, j)) >= (int) (max_color * 0.8)) {
//                            red += Color.red(bitmap.getPixel(i, j));
//                            green += Color.green(bitmap.getPixel(i, j));
//                            blue += Color.blue(bitmap.getPixel(i, j));
//                            num++;
//                        }
//                    }
//                }
//            }
//        }
//        try {
//            avg_red = red / num;
//            avg_green = green / num;
//            avg_blue = blue / num;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        avg_red = Color.red(bitmap.getPixel(min_X,min_Y))+Color.red(bitmap.getPixel(max_X,max_Y))/2;
//        return bitmap;
//    }
    public static double getPixel(int x, int y, Bitmap bitmap) {
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight())
            return 0;
        else
            return bitmap.getPixel(x, y);
    }

    /**
     * @param bitmap
     * @author CJS
     * @time 2021/5/18  10:11
     * @describe The method makes the customized ROI for catching.
     */
    public static void catchColorEdge(Bitmap bitmap) {
        int i = bitmap.getWidth() / 2;
        int j = bitmap.getHeight() / 2;
        while (i > 0) {
//            if (bitmap.getPixel(i, j) == Color.RED)
//            if (!(Color.red(bitmap.getPixel(i, j)) < 50 && Color.blue(bitmap.getPixel(i, j)) < 50 && Color.green(bitmap.getPixel(i, j)) < 50)) {

            if ((Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100)) {
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
//            if (!(Color.red(bitmap.getPixel(i, j)) <50 && Color.blue(bitmap.getPixel(i, j)) < 50 && Color.green(bitmap.getPixel(i, j)) < 50)) {

            if (Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100) {
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
//            if (!(Color.red(bitmap.getPixel(i, j)) < 50 && Color.blue(bitmap.getPixel(i, j)) < 50 && Color.green(bitmap.getPixel(i, j)) < 50)) {

            if ((Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100)) {
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
//            if (!(Color.red(bitmap.getPixel(i, j)) < 50 && Color.blue(bitmap.getPixel(i, j)) < 50 && Color.green(bitmap.getPixel(i, j)) < 50)) {

            if ((Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100)) {
                edgeMaxY = --j;
                Log.d("MAX", "catchEdge: " + edgeMaxY);
                break;
            }
            j++;
        }

    }

    public static void scanEdge(Bitmap bitmap) {

        for (int i = bitmap.getWidth() / 2; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                if (!(Color.red(bitmap.getPixel(i, j)) < 100 && Color.blue(bitmap.getPixel(i, j)) < 100 && Color.green(bitmap.getPixel(i, j)) < 100)) {
                    catchAreapointX = i;
                    catchAreapointY = j;
                    break;
                }
            }
        }
        Log.d("MIN", "catchAreapointX" + catchAreapointX + " catchAreapointY" + catchAreapointY);
    }

    /**
     * @param R,G,B
     * @author CJS
     * @time 2021/6/2  10:08
     * @describe The method converts RGB to HSV.
     */
    public static void toHSV(int R, int G, int B) {
        H = 0;
        S = 0;
        S1 = 0;
        V = 0;
        double R2 = R / 255.0;
        double G2 = G / 255.0;
        double B2 = B / 255.0;
        int MAX = Math.max(Math.max(R, G), B);
        double Max = Double.MIN_VALUE;

        if (R2 > Max)
            Max = R2;
        if (G2 > Max)
            Max = G2;
        if (B2 > Max)
            Max = B2;
        double Min = Double.MAX_VALUE;
        if (R2 < Min)
            Min = R2;
        if (G2 < Min)
            Min = G2;
        if (B2 < Min)
            Min = B2;
        double Gap1 = Max - Min;
        double H_R = ((60 * (((G2 - B2) / Gap1))) * 10);
        double H_G = ((60 * ((B2 - R2) / Gap1 + 2)));

        if (Gap1 == 0) {
            H = 0;
        } else if (R2 == Max) {

//            Log.d("HSV1", "" +((int) ((60 * (((G2 - B2) / Gap1))) * 10)) );
            if (((int) ((60 * (((G2 - B2) / Gap1))) * 10)) < 0) {
                if (((int) ((60 * (((G2 - B2) / Gap1))) * -10)) % 10 >= 5) {
                    H = (int) (((60 * (((G2 - B2) / Gap1)))) - 1);
//                    Log.d("HSV1",""+H);

                } else
                    H = (int) ((60 * (((G2 - B2) / Gap1))));
            } else {
                if (((int) ((60 * (((G2 - B2) / Gap1))) * 10)) % 10 >= 5) {
                    H = (int) (((60 * (((G2 - B2) / Gap1)))) + 1);

                } else
                    H = (int) ((60 * (((G2 - B2) / Gap1))));
            }
        } else if (Max == G2) {
            if (((int) (((60 * ((B2 - R2) / Gap1 + 2))) * 10)) < 0) {
                if (((int) (((60 * ((B2 - R2) / Gap1 + 2))) * -10)) % 10 >= 5)
                    H = (int) ((60 * ((B2 - R2) / Gap1 + 2)) - 1);

                else
                    H = (int) (60 * ((B2 - R2) / Gap1 + 2));
            } else {
                if (((int) (((60 * ((B2 - R2) / Gap1 + 2))) * 10)) % 10 >= 5)
                    H = (int) ((60 * ((B2 - R2) / Gap1 + 2)) + 1);
                else
                    H = (int) (60 * ((B2 - R2) / Gap1 + 2));
            }
        } else {
            if (((int) ((60 * ((R2 - G2) / Gap1 + 4)) * 10)) < 0) {
                if (((int) ((60 * ((R2 - G2) / Gap1 + 4)) * -10)) % 10 >= 5)
                    H = (int) ((60 * ((R2 - G2) / Gap1 + 4)) - 1);
                else
                    H = (int) (60 * ((R2 - G2) / Gap1 + 4));
            } else {
                if (((int) ((60 * ((R2 - G2) / Gap1 + 4)) * 10)) % 10 >= 5)
                    H = (int) ((60 * ((R2 - G2) / Gap1 + 4)) + 1);
                else
                    H = (int) (60 * ((R2 - G2) / Gap1 + 4));
            }
        }

        if (Max == 0)
            S = 0;
        else {

            if (((Gap1 / Max) * 1000) % 10 >= 5) {
                S = (int) (((Gap1 / Max) * 100) + 1);
                S1 = (((Gap1 / Max) * 100));
            } else {

                S = (int) ((Gap1 / Max) * 100);
                S1 = (((Gap1 / Max) * 100));
            }
        }
        if ((Max * 1000) % 10 >= 5) {
            V = (int) ((Max * 100) + 1);
            V1 = ((Max * 100));
        } else {
            V = (int) (Max * 100);
            V1 = (Max * 100);
        }

        if (H < 0)
            H = 360 + H;
    }

    public static double[] toLAB(int R, int G, int B) {
        //normalization
        double R_n = R / 255.0;
        double G_n = G / 255.0;
        double B_n = B / 255.0;
        double[] color_Matrix = new double[3];
        double[] color_Matrix_with_gamma = new double[3];
        double[] XYZ_Matrix = new double[3];
        double[] normalized_XYZ_Matrix = new double[3];
        double[] converted_XYZ_Matrix = new double[3];
        double[] LAB_Matrix = new double[3];
        color_Matrix[0] = R_n;
        color_Matrix[1] = G_n;
        color_Matrix[2] = B_n;
        //GAMMA converting
        int i = 0;
        for (double c : color_Matrix
        ) {
            if (c > 0.04045) {
                color_Matrix_with_gamma[i] = Math.pow(((color_Matrix[i] + 0.055) / 1.055), 2.4);
            } else {
                color_Matrix_with_gamma[i] = color_Matrix[i] / 12.92;
            }
            i++;
        }
        XYZ_Matrix[0] = 0.412453 * color_Matrix_with_gamma[0] + 0.357580 * color_Matrix_with_gamma[1] + 0.180423 * color_Matrix_with_gamma[2];
        XYZ_Matrix[1] = 0.212671 * color_Matrix_with_gamma[0] + 0.715160 * color_Matrix_with_gamma[1] + 0.072169 * color_Matrix_with_gamma[2];
        XYZ_Matrix[2] = 0.019334 * color_Matrix_with_gamma[0] + 0.119193 * color_Matrix_with_gamma[1] + 0.950227 * color_Matrix_with_gamma[2];
        normalized_XYZ_Matrix[0] = XYZ_Matrix[0] / 0.95047;
        normalized_XYZ_Matrix[1] = XYZ_Matrix[1] / 1.0;
        normalized_XYZ_Matrix[2] = XYZ_Matrix[2] / 1.08883;
        i = 0;
        for (double c : normalized_XYZ_Matrix
        ) {
            if (c > Math.pow(6.0 / 29.0, 3)) {
                converted_XYZ_Matrix[i] = Math.pow(c, 1.0 / 3.0);
            } else {
                converted_XYZ_Matrix[i] = 1.0 / 3.0 * Math.pow((29.0 / 6.0), 2) * c + (16.0 / 116.0);
            }
            i++;

        }
        LAB_Matrix[0] = 116 * converted_XYZ_Matrix[1] - 16;
        LAB_Matrix[1] = 500 * (converted_XYZ_Matrix[0] - converted_XYZ_Matrix[1]);
        LAB_Matrix[2] = 200 * (converted_XYZ_Matrix[1] - converted_XYZ_Matrix[2]);

        return LAB_Matrix;
    }
}

