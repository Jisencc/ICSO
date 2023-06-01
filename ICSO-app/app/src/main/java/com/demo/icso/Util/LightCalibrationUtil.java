package com.demo.icso.Util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * created by CJS on 2021/6/23 13:51
 * Uneven lightness calibration
 */
public class LightCalibrationUtil {
    /**
     * @author CJS
     * @time 2021/6/23  13:59
     * @describe
     * @param bitmapl
     */
    private static int H, S, V;
    private static double[][] ValueArray;
    private static double[][] ReValueArray1;
    private static double[][] ReValueArray2;
    private static double[][] ReValueArray3;
    private static double[][] EndingValueArray;
    private static int[][] HArray;
    private static int[][] SArray;
    private static double Add = 0;
    private static double Average = 0;
    private static double Variance = 0;
    private static double Add_Square = 0;
    private static double[][] GammaValueArray;
    private static int[] BitmapArray;

    public static Bitmap getIlluminationComponent(Bitmap bitmap) {
        Bitmap bitmapl = GrayscaleUtil.compress(bitmap, 201, 201);
        int[] illuminationArray = new int[bitmapl.getWidth() * bitmapl.getHeight()];
        ValueArray = new double[bitmapl.getWidth()][bitmapl.getHeight()];
        BitmapArray = new int[bitmapl.getWidth() * bitmapl.getHeight()];
        ReValueArray1 = new double[bitmapl.getWidth()][bitmapl.getHeight()];
        ReValueArray2 = new double[bitmapl.getWidth()][bitmapl.getHeight()];
        ReValueArray3 = new double[bitmapl.getWidth()][bitmapl.getHeight()];
        EndingValueArray = new double[bitmapl.getWidth()][bitmapl.getHeight()];
        GammaValueArray = new double[bitmapl.getWidth()][bitmapl.getHeight()];
        HArray = new int[bitmapl.getWidth()][bitmapl.getHeight()];
        SArray = new int[bitmapl.getWidth()][bitmapl.getHeight()];
        bitmapl.getPixels(illuminationArray, 0, bitmapl.getWidth(), 0, 0, bitmapl.getWidth(), bitmapl.getHeight());
        for (int i = 0; i < bitmapl.getWidth(); i++) {
            for (int j = 0; j < bitmapl.getHeight(); j++) {
                toHSV(Color.red(illuminationArray[i + j * bitmapl.getWidth()]), Color.green(illuminationArray[i + j * bitmapl.getWidth()]), Color.blue(illuminationArray[i + j * bitmapl.getWidth()]));
                ValueArray[i][j] = V / 100.0;
                HArray[i][j] = H;
                SArray[i][j] = S;
                Add += ValueArray[i][j];
            }
        }
        Average = Add / (bitmapl.getWidth() * bitmapl.getHeight());
        for (int i = 0; i < bitmapl.getWidth(); i++) {
            for (int j = 0; j < bitmapl.getHeight(); j++) {
                Add_Square += (ValueArray[i][j] - Average) * (ValueArray[i][j] - Average);
            }
        }
        Variance = Add_Square / (bitmapl.getWidth() * bitmapl.getHeight());

        createGuassKernel(bitmapl.getWidth(), bitmapl);
        for (int i = 0; i < bitmapl.getWidth(); i++) {
            for (int j = 0; j < bitmapl.getHeight(); j++) {
                int[] result = hsb2rgb(HArray[i][j], SArray[i][j], GammaValueArray[i][j]);
                BitmapArray[i + j * bitmapl.getWidth()] = Color.rgb(result[0], result[1], result[2]);
                if (i > 100 && j > 100 && i < 150 && j < 150)
                    Log.d("guass1", "R" + result[0] + "  G" + result[1] + " B" + result[2] + "  i:" + i + "    j" + j + "  H" + HArray[i][j] + "S   " + SArray[i][j] + "     V" + GammaValueArray[i][j]);
//                Log.d("End", "R"+result[0]+"  G"+result[1]+" B"+result[2]+"  i:"+i+"    j"+j+"  H"+HArray[i][j]+"S   "+SArray[i][j]+"     V"+GammaValueArray[i][j]);

            }
        }
        Bitmap bitmap1 = Bitmap.createBitmap(BitmapArray, bitmapl.getWidth(), bitmapl.getHeight(), Bitmap.Config.ARGB_8888);
        return bitmap1;
    }

    /**
     * @param Hsize
     * @author CJS
     * @time 2021/6/23  14:56
     * @describe
     */
    public static void createGuassKernel(int Hsize, Bitmap bitmap) {
//        Bitmap bitmap = GrayscaleUtil.compress(bitmapl, 301, 301);
        double power = 0;
        double sum = 0;
        double theta1 = 20;
        double theta2 = 72;
        double theta3 = 250;
        if (Hsize % 2 == 0)
            Hsize--;
//        Hsize = 11;
        int X_center = 0, Y_center = 0;
        X_center = Hsize / 2;
        Y_center = X_center;
        double[][] Guass = new double[Hsize][Hsize];
        double[][] ReGuass = new double[Hsize][Hsize];
        for (int t = 0; t < 3; t++) {
            sum = 0;
            for (int i = 0; i < Hsize; i++) {
                for (int j = 0; j < Hsize; j++) {

                    if (t == 0) {
                        power = -1.0 * ((i - X_center) * (i - X_center) + (j - Y_center) * (j - Y_center)) / (2.0 * (theta1 * theta1));
//                        Guass[i][j] = (1 / (2 * Math.PI * theta1)) * Math.pow(Math.E, power);
                        Guass[i][j] = Math.pow(Math.E, power);
                    }
                    if (t == 1) {
                        power = -1.0 * ((i - X_center) * (i - X_center) + (j - Y_center) * (j - Y_center)) / (2.0 * (theta2 * theta2));
//                        Guass[i][j] = (1 / (2 * Math.PI * theta2)) * Math.pow(Math.E, power);
                        Guass[i][j] = Math.pow(Math.E, power);
                    }
                    if (t == 2) {
                        power = -1.0 * ((i - X_center) * (i - X_center) + (j - Y_center) * (j - Y_center)) / (2.0 * (theta3 * theta3));
//                        Guass[i][j] = (1 / (2 * Math.PI * theta3)) * Math.pow(Math.E, power);
                        Guass[i][j] = Math.pow(Math.E, power);

                    }
//                    Log.d("guass", Guass[i][j] + "  " + i + j + "  " + t);
                    sum = sum + Guass[i][j];
                }
            }

            for (int i = 0; i < Hsize; i++)
                for (int j = 0; j < Hsize; j++) {
                    ReGuass[i][j] = Guass[i][j] / sum;
//                    Log.d("make", ReGuass[i][j] + "  " + i + " "+j + "  " + t);

                }
            sum = 0;

            int row = 0, col = 0;
            int avgX, avgY;
            avgX = Hsize / 2;
            avgY = avgX;
            double sum_guass = 0;

            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    for (int m = 0; m < Hsize; m++) {
                        for (int n = 0; n < Hsize; n++) {
                            row = i + (m - avgX);
                            col = j + (n - avgY);
                            if (row <= 0)
                                row = 0;
                            else if (row > (bitmap.getWidth() - 1))
                                row = bitmap.getWidth() - 1;
                            if (col <= 0)
                                col = 0;
                            else if (col > (bitmap.getHeight() - 1))
                                col = bitmap.getHeight() - 1;
                            sum_guass = sum_guass + ValueArray[row][col] * ReGuass[m][n];
                        }

                    }
                    if (t == 0) {
                        ReValueArray1[i][j] = sum_guass;
                    } else if (t == 1) {
                        ReValueArray2[i][j] = sum_guass;
                    } else if (t == 2) {
                        ReValueArray3[i][j] = sum_guass;
                    }
                    sum_guass = 0;
                }


            }

        }
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                EndingValueArray[i][j] = (1.0 / 3.0) * (ReValueArray1[i][j] + ReValueArray2[i][j] + ReValueArray3[i][j]);
            }
        }
        GammaCalibration(bitmap);
    }

    public static void GammaCalibration(Bitmap bitmap) {
        double gamma = 0;
        double illuminationAvg = 0;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                illuminationAvg += EndingValueArray[i][j];
            }
        }
        illuminationAvg = illuminationAvg / (bitmap.getWidth() * bitmap.getHeight());

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                gamma = Math.pow(1, ((illuminationAvg - EndingValueArray[i][j]) / illuminationAvg)+ (illuminationAvg - EndingValueArray[i][j])/illuminationAvg*illuminationAvg);
//                gamma = (illuminationAvg - EndingValueArray[i][j]) / illuminationAvg;
                GammaValueArray[i][j] = Math.pow((ValueArray[i][j] ), gamma);
//                GammaValueArray[i][j] = ValueArray[i][j] * gamma;
                if (i > 100 && j > 100 && i < 150 && j < 150)
                    Log.d("gama", "     V" + GammaValueArray[i][j]);


            }
        }


    }

    public static int[] hsb2rgb(double h, double s, double v) {
//        v = v / 100.0;
        s = s / 100;
        assert Double.compare(h, 0.0) >= 0 && Double.compare(h, 360.0) <= 0;
        assert Double.compare(s, 0.0) >= 0 && Double.compare(s, 1.0) <= 0;
        assert Double.compare(v, 0.0) >= 0 && Double.compare(v, 1.0) <= 0;

        double r = 0, g = 0, b = 0;
        int i = (int) ((h / 60) % 6);
        double f = (h / 60) - i;
        double p = v * (1 - s);
        double q = v * (1 - f * s);
        double t = v * (1 - (1 - f) * s);
        switch (i) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
            default:
                break;
        }
        return new int[]{(int) (r * 255.0), (int) (g * 255.0),
                (int) (b * 255.0)};
    }

    /**
     * @param R,G,B
     * @author CJS
     * @time 2021/6/2  10:08
     * @describe
     */
    public static void toHSV(int R, int G, int B) {
        H = 0;
        S = 0;
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

            if (((int) ((60 * (((G2 - B2) / Gap1))) * 10)) < 0) {
                if (((int) ((60 * (((G2 - B2) / Gap1))) * -10)) % 10 >= 5) {
                    H = (int) (((60 * (((G2 - B2) / Gap1)))) - 1);


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
            if (((Gap1 / Max) * 1000) % 10 >= 5)
                S = (int) (((Gap1 / Max) * 100) + 1);
            else
                S = (int) ((Gap1 / Max) * 100);
        }
        if ((Max * 1000) % 10 >= 5)
            V = (int) ((Max * 100) + 1);
        else
            V = (int) (Max * 100);


        if (H < 0)
            H = 360 + H;
    }


}
