package com.demo.icso;


import static android.os.Environment.DIRECTORY_PICTURES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.TransactionTooLargeException;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;


import com.bumptech.glide.Glide;
import com.demo.icso.Bean.Calibration;

import com.demo.icso.Bean.MyPoint;
import com.demo.icso.Bean.PointColor;
import com.demo.icso.Bean.PointD;
import com.demo.icso.Bean.Scale;
import com.demo.icso.R;
import com.demo.icso.Util.LinearRegressionUtil;
import com.demo.icso.ml.ClinicModelOxy;
import com.demo.icso.Util.ColorAvgUtil;
import com.demo.icso.Util.EdgeRecognitionUtil;
import com.demo.icso.Util.LightCalibrationUtil;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


public class Clinic_Oxy_Activity extends Activity implements OnClickListener, PopupMenu.OnMenuItemClickListener, SensorEventListener {
    private static final int REQUEST_CAMERA = 1;
    private TextView tv_rgb;
    private ImageView iv_image;
    private File file;
    private Bitmap bitmap;
    private String TAG = "RGBActivity";
    private Button btnColor;
    public static final int NONE = 0;
    public static boolean Scaletag = false;
    //    public static final int PHOTOHRAPH = 1;
    public static final int PHOTOZOOM = 2;
    public static final int PHOTORESOULT = 3;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    public static final String TEMP_JPG_NAME = "temp.jpg";
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    private Uri cropImageUri;
    private int Scale_i = 0;
    private Button btnGetPhoto;
    private TextView avg_rgb;
    private EditText et_con;
    private List<Calibration> calibrations = new ArrayList<>();
    private List<PointD> pointDS = new ArrayList<>();
    private List<PointColor> pointColors = new ArrayList<>();
    private Button btn_cal;
    private Button btn_calright;
    private Button check_line;
    private Button recognition;
    private Button btn_quickinfo;
    private TextView tv_quickinfo;
    private ImageView iv_add;
    private ImageView iv_gamma;
    private boolean flag = false;
    private boolean calibration_check = false;
    private double result;
    private TextView tv_showconcentration;
    private Intent pointIntent;
    private boolean finish_tag = false;
    private Button reset_btn;
    private Uri imageuri;
    private Uri camerauri;
    private Button btn_changecolor;
    private static int color_status = 0;
    private Button btn_ill;
    private List<Scale> ScaleList = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView tv_lightSensor;
    private Boolean clinic_flag = false;
    private float[][] clinic_input = new float[1][15];
    private int sampling_num = 1;
    private StringBuilder currentPosition;
    boolean mapFlag = false;
    float input_r = 0, input_g = 0, input_b = 0, input_H = 0, input_S = 0, input_V = 0, input_L = 0, input_A = 0, input_B = 0, input_R = 0, input_G = 0, input_B1 = 0, input_H_T = 0, input_S_T = 0, input_V_T = 0;
    private final float[] mean = new float[]

            {
                    153.17816092f, 92.97126437f, 87.73563218f, 364.55747127f, 44.0f,
                    60.12068966f, 177.54022988f, 136.28160919f, 111.08045977f, 22.94827586f,
                    38.63793103f, 69.61494253f, 60.16968391f, 14.2141092f, 20.33468391f
            };
    private final float[] var = new float[]

            {
                    631.93569167f, 441.23863788f, 521.6274277f, 47.30799972f, 103.17624521f,
                    96.1789206f, 546.55489497f, 638.7501982f, 604.9896948f, 26.78468094f,
                    94.52599419f, 83.4934932f, 83.15133444f, 32.08070261f, 31.83263271f
            };


    String latitude;
    String longtitude;
    String country;
    String province;
    String city;
    String district;
    String Street;
    String Addrstr;


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);
//        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_clinic_oxy);
        btn_cal = findViewById(R.id.clinic_measurement);
        btn_calright = findViewById(R.id.clinic_calibration_right);
//        iv_gamma = findViewById(R.id.iv_gamma);
        check_line = findViewById(R.id.clinic_check_line);
        tv_quickinfo = findViewById(R.id.clinic_tv_quickinfo);
//        recognition = findViewById(R.id.recognition);
//        btn_quickinfo = findViewById(R.id.btn_quickinfo);
//        btn_quickinfo.setOnClickListener(this);
//        et_con = (EditText) findViewById(R.id.concentration_value);
        tv_rgb = (TextView) findViewById(R.id.clinic_textview);
        reset_btn = findViewById(R.id.clinic_reset);
//        btnColor = (Button) findViewById(R.id.btnColor);
//        btnGetPhoto = (Button) findViewById(R.id.getColor);
        iv_image = (ImageView) findViewById(R.id.clinic_iv_image);
//        btn_ill = (Button) findViewById(R.id.illumination_cali);
        tv_showconcentration = findViewById(R.id.clinic_concentration_show);
//        btn_changecolor = findViewById(R.id.change_color);
        avg_rgb = findViewById(R.id.clinic_avg_rgb);
        iv_add = findViewById(R.id.clinic_iv_add);
        iv_add.setOnClickListener(this);

        reset_btn.setOnClickListener(this);
        btn_cal.setOnClickListener(this);
        fullScreen(this);
        btn_calright.setOnClickListener(this);
//        recognition.setOnClickListener(this);
        check_line.setOnClickListener(this);
//        btn_changecolor.setOnClickListener(this);
//        btn_ill.setOnClickListener(this);
//        Toast.makeText(this, "" + new Date().toString(), Toast.LENGTH_SHORT).show();
//        Log.d("ttttt", new Date().getMonth() + 1 + "");

        tv_lightSensor = findViewById(R.id.tv_lightsensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            Log.e("TAG", "===============" + sensor);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
        try {


            iv_image.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int x = (int) event.getX();

                    int y = (int) event.getY();

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        int color = 0;
                        if (x >= 0 && y < bitmap.getHeight() && y >= 0 && x < bitmap.getWidth()) {
//                        ColorAvgUtil.ColorAvgOperator2(bitmap,x,y);
                            color = bitmap.getPixel(x, y);

                        }

                        int r = Color.red(color);
                        int g = Color.green(color);
                        int b = Color.blue(color);
                        int a = Color.alpha(color);
                        double[] m = ColorAvgUtil.toLAB(r, g, b);
                        ColorAvgUtil.toHSV(r, g, b);

                        tv_rgb.setText("a: " + a + ", r: " + r + ", g: " + g + ", b: "
                                + b + ", X: " + x + ", Y: " + y + ", L: " + (float) m[0] + ", a: " + (float) m[1] + ", b: " + (float) m[2] + ", H: " + ColorAvgUtil.H + ", S: " + ColorAvgUtil.S + ", V: " + ColorAvgUtil.V);
                        if (clinic_flag && sampling_num < 8) {
                            if (sampling_num < 4) {
                                input_r = input_r + r;
                                input_g = input_g + g;
                                input_b = input_b + b;
                                if (ColorAvgUtil.H >= 0 && ColorAvgUtil.H <= 30)
                                    input_H = input_H + ColorAvgUtil.H + 360;
                                else
                                    input_H = input_H + ColorAvgUtil.H;
                                input_S = input_S + ColorAvgUtil.S;
                                input_V = input_V + ColorAvgUtil.V;
                                sampling_num++;
                            } else if (sampling_num >= 4 && sampling_num < 7) {
                                input_R = input_R + r;
                                input_G = input_G + g;
                                input_B1 = input_B1 + b;
                                input_H_T = input_H_T + ColorAvgUtil.H;
                                input_S_T = input_S_T + ColorAvgUtil.S;
                                input_V_T = input_V_T + ColorAvgUtil.V;
                                input_L = input_L + (float) m[0];
                                input_A = input_A + (float) m[1];
                                input_B = input_B + (float) m[2];
                                sampling_num++;
                            }
                        }

                        if (sampling_num == 7) {
                            tv_rgb.setText("Done!");
                            clinic_input[0] = new float[]{(input_r / 3 - (float) mean[0]) / (float) Math.sqrt(var[0]), (input_g / 3 - (float) mean[1]) / (float) Math.sqrt(var[1]), (input_b / 3 - (float) mean[2]) / (float) Math.sqrt(var[2]), (input_H / 3 - (float) mean[3]) / (float) Math.sqrt(var[3]), (input_S / 3 - (float) mean[4]) / (float) Math.sqrt(var[4]), (input_V / 3 - (float) mean[5]) / (float) Math.sqrt(var[5]), (input_R / 3 - (float) mean[6]) / (float) Math.sqrt(var[6]), (input_G / 3 - (float) mean[7]) / (float) Math.sqrt(var[7]), (input_B1 / 3 - (float) mean[8]) / (float) Math.sqrt(var[8]), (input_H_T / 3 - (float) mean[9]) / (float) Math.sqrt(var[9]), (input_S_T / 3 - (float) mean[10]) / (float) Math.sqrt(var[10]), (input_V_T / 3 - (float) mean[11]) / (float) Math.sqrt(var[11]), (input_L / 3 - (float) mean[12]) / (float) Math.sqrt(var[12]), (input_A / 3 - (float) mean[13]) / (float) Math.sqrt(var[13]), (input_B / 3 - (float) mean[14]) / (float) Math.sqrt(var[14])};

                            clinic_flag = false;
                        }


                        if (Scaletag) {
                            ScaleList.add(new Scale(x, y, Scale_i));
                        }
                        Scale_i++;
//                            btnColor.setTextColor(Color.rgb(r, g, b));
//                    tv_showconcentration.setText("r=" + ColorAvgUtil.avg_red + ",g=" + ColorAvgUtil.avg_green+ ",b="
//                            + ColorAvgUtil.avg_blue);
//                        clinic_input[0]=new float[]{
//                                1.10674965e+00f,  9.53491370e-01f,  1.06240173e+00f, -5.17218569e-01f,
//                                -5.90692411e-01f,  1.10933168e+00f, -1.08656604e-01f, -8.81619178e-01f,
//                                -9.11258034e-01f, -1.08493090e+00f,  1.20292774e+00f, -1.03778736e-01f,
//                                -6.76594250e-01f,  1.49233456e+00f,  7.08723854e-01f,
//                        };
                    }

                    return true;
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 打开相册
    private void openAlbum() {
      /*  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image*//*");*/

        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
     /*
       Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);*/
        startActivityForResult(intent, PHOTOZOOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Log.e("TAG", "---------" + FileProvider.getUriForFile(this, "com.demo.icso.fileprovider", file));

            startPhotoZoom(camerauri);
            //iv_image.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));


        }
        try {

            if (resultCode == NONE)
                return;

            if (data == null)
                return;


            if (requestCode == PHOTOZOOM) {
              /*  Uri image = data.getData();
                Toast.makeText(MymessageActivity.this,image+"", Toast.LENGTH_LONG).show();*/

                if (data != null) {
                    startPhotoZoom(data.getData());
                }

            }



            if (requestCode == CAMERA_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    if (isAndroidQ) {

                        iv_image.setImageURI(mCameraUri);
//                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCameraUri));
//                        bitmap = big(bitmap);
//                        bitmap = EdgeRecognitionUtil.SobelOperator(bitmap);
//
//                        iv_image.setImageBitmap(bitmap);
//                            iv_image.setImageBitmap(big(BitmapFactory.decodeFile(mCameraImagePath)));
                    } else {

                        iv_image.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(mCameraUri)));
//                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCameraUri));
//                        bitmap = big(bitmap);
//                        bitmap = EdgeRecognitionUtil.SobelOperator(bitmap);
//
//                        iv_image.setImageBitmap(bitmap);
//                        iv_image.setImageBitmap(EdgeRecognitionUtil.SobelOperator(big(BitmapFactory.decodeFile(mCameraImagePath))));
                    }
                } else {
                    Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show();
                }
            }

            if (requestCode == PHOTORESOULT) {


//                Toast.makeText(getApplicationContext(), data.getIntExtra("outputX", 1) + "", Toast.LENGTH_SHORT).show();
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
//                        bitmap = big(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                bitmap = EdgeRecognitionUtil.SobelOperator(bitmap);
//                                bitmap = ColorAvgUtil.ColorAvgOperator1(bitmap);

//                                final Bitmap bitmap1 = LightCalibrationUtil.getIlluminationComponent(bitmap);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        iv_image.setImageBitmap(bitmap);
//                                        iv_gamma.setImageBitmap(bitmap1);
//                                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, null,null));
//                                        startPhotoZoom(uri);
//                                        Toast.makeText(getApplicationContext(), "MinX" + ColorAvgUtil.edgeMinX + "MaxX" + ColorAvgUtil.edgeMaxX + "MinY" + ColorAvgUtil.edgeMinY + "MAXY" + ColorAvgUtil.edgeMaxY, Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getApplicationContext(), "R：" + EdgeRecognitionUtil.avg_red + "G :" + EdgeRecognitionUtil.avg_green + "B :" + EdgeRecognitionUtil.avg_blue, Toast.LENGTH_SHORT).show();
//                                        avg_rgb.setText("R：" + ColorAvgUtil.avg_red + " G :" + ColorAvgUtil.avg_green + " B :" + ColorAvgUtil.avg_blue + " H :" + ColorAvgUtil.avg_H + " S :" + ColorAvgUtil.avg_S + " V :" + ColorAvgUtil.avg_V + " G1:" + ColorAvgUtil.avg_G1 + " S1:" + ColorAvgUtil.avg_S1 + " V1:" + ColorAvgUtil.avg_V1+"R1:" + ColorAvgUtil.avg_R1+" B1:" + ColorAvgUtil.avg_B1);
//                                        avg_rgb.setText("\n"+"Oxygen saturation: 96.41%");
//                                        avg_rgb.setText("R：" + ColorAvgUtil.avg_red + " G: " + ColorAvgUtil.avg_green + " B: " + ColorAvgUtil.avg_blue + " H: " + ColorAvgUtil.avg_H + " S: " + ColorAvgUtil.avg_S + " V: " + ColorAvgUtil.avg_V);

                                        flag = true;
                                        tv_showconcentration.setText("");
                                    }
                                });

                            }
                        }).start();

                        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        comp(bitmap);
                        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

//                        Toast.makeText(this, "R："+red/num+"G :"+green/num+"B :"+blue/num, Toast.LENGTH_SHORT).show();

                    }
                   /* logoName = FileUtils.getFilename(MainAppUtil.getCustom().getSusername());
                    FileUtils.writeFile(Constants.LOGO_CACHE_PATH, logoName, photo);*/
                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(4f, 4f);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    private Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {

            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 800f;
        float ww = 500f;

        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);
    }


    public void startPhotoZoom(Uri uri) {
        File CropPhoto = new File(getExternalCacheDir(), "crop.jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            CropPhoto = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES), "crop_image.jpg");
        } else {
            CropPhoto = new File(getExternalCacheDir(), "crop_image.jpg");
        }
        try {
            if (CropPhoto.exists()) {
                CropPhoto.delete();
            }
            CropPhoto.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        cropImageUri = Uri.fromFile(CropPhoto);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }



        intent.putExtra("crop", true);
        intent.putExtra("scale", true);

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);



        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 1000);

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, PHOTORESOULT);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            } // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, getIntent());
        super.finish();
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }



    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        int hasCameraPermission1 = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (hasCameraPermission1 != PackageManager.PERMISSION_GRANTED||hasCameraPermission!= PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
//
//        }

        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED && hasCameraPermission1 == PackageManager.PERMISSION_GRANTED) {

            useCamera();
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                openCamera();
//            } else {
//
//                Toast.makeText(this, "No permission!", Toast.LENGTH_LONG).show();
//            }

//        }
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCamera();
        } else {

            Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();

        }

    }

    private Uri mCameraUri;
    private String mCameraImagePath;
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    public static final int CAMERA_REQUEST_CODE = 4;

//    private Uri createImageUri() {
//        String status = Environment.getExternalStorageState();
//        if (status.equals(Environment.MEDIA_MOUNTED)) {
//            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
//        } else {
//            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
//        }
//    }
//    private File createImageFile() throws IOException {
//        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        if (!storageDir.exists()) {
//            storageDir.mkdir();
//        }
//        File tempFile = new File(storageDir, imageName+".jpg");
//        tempFile.createNewFile();
//        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
//            return null;
//        }
//        return tempFile;
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.clinic_measurement:
                if (sampling_num > 0) {

                    try {
                        ClinicModelOxy model = ClinicModelOxy.newInstance(getApplicationContext());

                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 15}, DataType.FLOAT32);
//                        inputFeature0.loadBuffer(ByteBuffer.allocate(clinic_input.getByteCount()));

                        inputFeature0.loadArray(clinic_input[0]);
                        // Runs model inference and gets result.
                        ClinicModelOxy.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//                        tv_showconcentration.setText("Oxygen saturation: "+new StringBuilder().append(outputFeature0.getFloatValue(0)).append("").toString());
                        if ((outputFeature0.getFloatValue(0))>100){
                            tv_showconcentration.setText(String.format("Oxygen saturation: %d",100)+"%");                        Log.d("clinic", "onClick: " + clinic_input[0][0] + " " + clinic_input[0][1] + " " + clinic_input[0][2] + " " + clinic_input[0][3] + " " + clinic_input[0][4] + " " + clinic_input[0][5] + " " + clinic_input[0][6] + " " + clinic_input[0][7] + " " + clinic_input[0][8] + " " + clinic_input[0][9] + " " + clinic_input[0][10] + " " + clinic_input[0][11] + " " + clinic_input[0][12] + " " + clinic_input[0][13] + " " + clinic_input[0][14] + " ");

                        }
                        else
                        tv_showconcentration.setText(String.format("Oxygen saturation: %.2f",outputFeature0.getFloatValue(0))+"%");                        Log.d("clinic", "onClick: " + clinic_input[0][0] + " " + clinic_input[0][1] + " " + clinic_input[0][2] + " " + clinic_input[0][3] + " " + clinic_input[0][4] + " " + clinic_input[0][5] + " " + clinic_input[0][6] + " " + clinic_input[0][7] + " " + clinic_input[0][8] + " " + clinic_input[0][9] + " " + clinic_input[0][10] + " " + clinic_input[0][11] + " " + clinic_input[0][12] + " " + clinic_input[0][13] + " " + clinic_input[0][14] + " ");
                        // Releases model resources if no longer used.
                        model.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        // TODO Handle the exception
                    }
                } else {
                    Toast.makeText(this, "Please check the input data!", Toast.LENGTH_SHORT).show();
                }


                break;

            case R.id.check_line:
                break;
            case R.id.clinic_reset:
                clinic_flag = true;
                sampling_num = 1;
                input_r = 0;
                input_g = 0;
                input_b = 0;
                input_H = 0;
                input_S = 0;
                input_V = 0;
                input_L = 0;
                input_A = 0;
                input_B = 0;
                input_R = 0;
                input_G = 0;
                input_B1 = 0;
                input_H_T = 0;
                input_S_T = 0;
                input_V_T = 0;

                flag = false;
                calibration_check = false;
                color_status = 0;
                pointDS = new ArrayList<>();
                pointColors = new ArrayList<>();
                break;
            case R.id.change_color:
                break;

            case R.id.clinic_iv_add: {
                PopupMenu popup = new PopupMenu(this, iv_add);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.my_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            }
            case R.id.illumination_cali: {
//
                if (flag) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap1 = LightCalibrationUtil.getIlluminationComponent(bitmap);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒 E").format(new Date()), null));
                                        startPhotoZoom(uri);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(Clinic_Oxy_Activity.this, "Fail to create image!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();


                } else
                    Toast.makeText(this, "the image has not been loaded", Toast.LENGTH_SHORT).show();


            }

        }


    }

    @Override
    protected void onStop() {
        super.onStop();
//        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLocationClient.stop();
    }

    private void useCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/test/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();


        camerauri = FileProvider.getUriForFile(this, "com.demo.icso.fileprovider", file);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, camerauri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void applyWritePermission() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (Build.VERSION.SDK_INT >= 23) {
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);

            if (check == PackageManager.PERMISSION_GRANTED) {

                useCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 200);
            }
        } else {
            useCamera();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.take:
                checkPermissionAndCamera();
                break;
            case R.id.choose:
                openAlbum();
                break;
            case R.id.exit:

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Window window = activity.getWindow();
                View decorView = window.getDecorView();

                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);

//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (sensorManager != null) {
//            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;

        tv_lightSensor.setText("Illuminance===============" + values[0]);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (sensorManager != null) {
//            sensorManager.unregisterListener(this);
//        }
    }


}

