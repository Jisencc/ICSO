package com.demo.icso.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * author: CJS on 2021/4/29 12:52
 *
 */
public class GrayscaleUtil {
    public static Bitmap toGrayscale(Bitmap bitmap){
         int width,height;
         width = bitmap.getWidth();
         height = bitmap.getHeight();
         Bitmap bitmapforGray = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmapforGray);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(bitmap,0,0,paint);
        return bitmapforGray;


    }
    public static Bitmap compress(final Bitmap bm, int reqWidth, int reqHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        if (height > reqHeight || width > reqWidth) {
            float scaleWidth = (float) reqWidth / width;
            float scaleHeight = (float) reqHeight / height;
            float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
            bm.recycle();
            return result;
        }
        return bm;
    }
}
