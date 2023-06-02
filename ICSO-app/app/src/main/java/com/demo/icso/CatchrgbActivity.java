package com.demo.icso;


import static android.os.Environment.DIRECTORY_PICTURES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
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
import com.demo.icso.Util.ColorAvgUtil;
import com.demo.icso.Util.EdgeRecognitionUtil;
import com.demo.icso.Util.LightCalibrationUtil;
import com.demo.icso.Util.LinearRegressionUtil;


public class CatchrgbActivity extends Activity implements OnClickListener, PopupMenu.OnMenuItemClickListener, SensorEventListener {
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
    private StringBuilder currentPosition;
    boolean mapFlag = false;
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


        setContentView(R.layout.activity_rgb);
        btn_cal = findViewById(R.id.calibration);
        btn_calright = findViewById(R.id.calibration_right);

        check_line = findViewById(R.id.check_line);
        tv_quickinfo = findViewById(R.id.tv_quickinfo);
        recognition = findViewById(R.id.recognition);
//        btn_quickinfo = findViewById(R.id.btn_quickinfo);
//        btn_quickinfo.setOnClickListener(this);
        et_con = (EditText) findViewById(R.id.concentration_value);
        tv_rgb = (TextView) findViewById(R.id.textview);
        reset_btn = findViewById(R.id.reset);
//        btnColor = (Button) findViewById(R.id.btnColor);
//        btnGetPhoto = (Button) findViewById(R.id.getColor);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        btn_ill = (Button) findViewById(R.id.illumination_cali);
        tv_showconcentration = findViewById(R.id.concentration_show);
        btn_changecolor = findViewById(R.id.change_color);
        avg_rgb = findViewById(R.id.avg_rgb);
        iv_add = findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        reset_btn.setOnClickListener(this);
        btn_cal.setOnClickListener(this);
        fullScreen(this);
        btn_calright.setOnClickListener(this);
        recognition.setOnClickListener(this);
        check_line.setOnClickListener(this);
        btn_changecolor.setOnClickListener(this);
        btn_ill.setOnClickListener(this);
        tv_lightSensor = findViewById(R.id.tv_lightsensor);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        btnGetPhoto.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                checkPermissionAndCamera();
//            }
//        });
//        btnColor.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                openAlbum();
//            }
//        });
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
                        if (Scaletag) {
                            ScaleList.add(new Scale(x, y, Scale_i));
                        }
                        Scale_i++;
                    }

                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
                        bitmap = big(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                bitmap = EdgeRecognitionUtil.SobelOperator(bitmap);
                                bitmap = ColorAvgUtil.ColorAvgOperator1(bitmap);

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
                                        avg_rgb.setText("R：" + ColorAvgUtil.avg_red + " G: " + ColorAvgUtil.avg_green + " B: " + ColorAvgUtil.avg_blue + " H: " + ColorAvgUtil.avg_H + " S: " + ColorAvgUtil.avg_S + " V: " + ColorAvgUtil.avg_V);

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
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
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

            CropPhoto = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES),"crop_image.jpg");
        }else {
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

    /**
     * Check permission
     *
     */

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
//                Toast.makeText(this, "Deny", Toast.LENGTH_LONG).show();
//            }

//        }
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCamera();
        } else {

            Toast.makeText(this, "permission is needed!", Toast.LENGTH_SHORT).show();

        }

    }


    private Uri mCameraUri;


    private String mCameraImagePath;


    private boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    public static final int CAMERA_REQUEST_CODE = 4;

    /**
     * Capture
     */
//    private void openCamera() {
//        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (captureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            Uri photoUri = null;
//
//            if (isAndroidQ) {
//
//                photoUri = createImageUri();
//            } else {
//                try {
//                    photoFile = createImageFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (photoFile != null) {
//                    mCameraImagePath = photoFile.getAbsolutePath();
//                    Toast.makeText(this, ""+photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                        photoUri = FileProvider.getUriForFile(this, "com.demo.icso.fileprovider", photoFile);
//                        Toast.makeText(this, ""+photoUri, Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        photoUri = Uri.fromFile(photoFile);
//                        Toast.makeText(this, ""+photoUri
//                                , Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            mCameraUri = photoUri;
//            if (photoUri != null) {
//                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
////                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                Toast.makeText(this, ""+mCameraUri, Toast.LENGTH_SHORT).show();
//                startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
//            }
//        }
//    }
//    private void openCamera() {
//        File outputImage = new File(getExternalCacheDir(),"output_image"+new Date()+".jpg");
//        try {
//            if (outputImage.exists()){
//                outputImage.delete();
//            }
//            outputImage.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (Build.VERSION.SDK_INT>=24){
//            imageuri =
//        }
//    }


//    private Uri createImageUri() {
//        String status = Environment.getExternalStorageState();
//
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
            case R.id.calibration:
                if (flag && !et_con.getText().toString().equals("")) {
//                    pointDS.add(new PointD( Double.valueOf(et_con.getText().toString()),EdgeRecognitionUtil.avg_green));
                    pointColors.add(new PointColor(Double.valueOf(et_con.getText().toString()), ColorAvgUtil.avg_red, ColorAvgUtil.avg_blue, ColorAvgUtil.avg_green,ColorAvgUtil.avg_V));
                    Toast.makeText(this, "Successfully submitting the calibration data, overall" + pointColors.size() + " data" + ColorAvgUtil.avg_red, Toast.LENGTH_SHORT).show();
                    Log.d("test1", "" + Double.valueOf(et_con.getText().toString()));
                    et_con.setText("");
                    flag = false;
                } else {
                    Toast.makeText(this, "Please input concentration or obtain color value", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.recognition:
                pointDS = new ArrayList<>();
                if (flag && calibration_check) {
                    if (color_status == 0) {
                        for (PointColor pointColor : pointColors)
                            pointDS.add(new PointD(pointColor.x, pointColor.y_red/pointColor.y_V));
//                            Toast.makeText(this, ""+pointColor.x+" "+pointColor.y_red, Toast.LENGTH_SHORT).show();

                        LinearRegressionUtil lineRegressionUtil = new LinearRegressionUtil(pointDS);
                        result = lineRegressionUtil.easyRegression((float)ColorAvgUtil.avg_red/ColorAvgUtil.avg_V);
//                        Toast.makeText(this, "Concentration：" + String.format("%.2f",result)+" μg/mL", Toast.LENGTH_SHORT).show();
                        tv_showconcentration.setText("Concentration：" + String.format("%.2f",result)+" μg/mL");
                        Log.d("test1", "Concentration：" + result);
//                        flag = false;
                    } else if (color_status == 1) {
                        for (PointColor pointColor : pointColors)
                            pointDS.add(new PointD(pointColor.x, pointColor.y_blue/pointColor.y_V));
                        LinearRegressionUtil lineRegressionUtil = new LinearRegressionUtil(pointDS);
                        result = lineRegressionUtil.easyRegression((float)ColorAvgUtil.avg_blue/ColorAvgUtil.avg_V);
//                        Toast.makeText(this, "Concentration：" + result, Toast.LENGTH_SHORT).show();
                        tv_showconcentration.setText("Concentration：" + String.format("%.2f",result)+" μg/mL");
                        Log.d("test1", "Concentration：" + result);
//                        flag = false;
                    } else if (color_status == 2) {
                        for (PointColor pointColor : pointColors)
                            pointDS.add(new PointD(pointColor.x, pointColor.y_green/pointColor.y_V));
                        LinearRegressionUtil lineRegressionUtil = new LinearRegressionUtil(pointDS);
                        result = lineRegressionUtil.easyRegression((float)ColorAvgUtil.avg_green/ColorAvgUtil.avg_V);
//                        Toast.makeText(this, lineRegressionUtil.getK() + "" + lineRegressionUtil.getB(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(this, "Concentration：" + result, Toast.LENGTH_SHORT).show();
                        tv_showconcentration.setText("Concentration：" + String.format("%.2f",result)+" μg/mL");

//                        flag = false;
                    }
                } else {
//
                    new AlertDialog.Builder(this)
                            .setTitle("Calibration")
                            .setMessage("Please load a sample!")
                            .setPositiveButton("Ok", null)
                            .show();
                }
                break;
            case R.id.calibration_right:
                if (pointColors.size() >= 3 && !calibration_check) {
                    new AlertDialog.Builder(this)
                            .setTitle("Calibration")
                            .setMessage("Please load a sample!")
                            .setPositiveButton("Ok", null)
                            .show();
                    if (color_status == 0) {
                        for (PointColor pointColor : pointColors) {
                            pointDS.add(new PointD(pointColor.x, pointColor.y_red/ColorAvgUtil.avg_V));
//                            Toast.makeText(this, "" + pointColor.x + " " + pointColor.y_red, Toast.LENGTH_SHORT).show();
                        }
                    } else if (color_status == 1) {
                        for (PointColor pointColor : pointColors)
                            pointDS.add(new PointD(pointColor.x, pointColor.y_blue/ColorAvgUtil.avg_V));
                    } else if (color_status == 2) {
                        for (PointColor pointColor : pointColors)
                            pointDS.add(new PointD(pointColor.x, pointColor.y_green/ColorAvgUtil.avg_V));
                    }
                    Toast.makeText(this, "demo", Toast.LENGTH_SHORT).show();
                    pointIntent = new Intent(this, LinearRegressionUtil.class);
                    pointIntent.putExtra("point", (Serializable) pointDS);
                    calibration_check = true;
                } else if (pointDS.size() < 3) {
                    new AlertDialog.Builder(this)
                            .setTitle("Calibration")
                            .setMessage("Not enough data")
                            .setPositiveButton("Ok", null)
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Calibration")
                            .setMessage("Successful calibration")
                            .setPositiveButton("Ok", null)
                            .show();
                }
                break;
            case R.id.check_line:
                if (calibration_check) {
//                    pointIntent = new Intent(this, LineChartAcitivity.class);
//                    pointIntent.putExtra("point", (Serializable) pointDS);
//                    startActivity(pointIntent);
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Calibration")
                            .setMessage("No calibration")
                            .setPositiveButton("Ok", null)
                            .show();
                }
                break;
            case R.id.reset:
                flag = false;
                calibration_check = false;
                color_status = 0;
                pointDS = new ArrayList<>();
                pointColors = new ArrayList<>();
                break;
            case R.id.change_color:
                pointDS = new ArrayList<>();
                if (color_status >= 0 && color_status <= 2) {
                    color_status++;

                }
                if (color_status > 2) {
                    color_status = 0;
                }
                if (color_status == 0) {
                    btn_changecolor.setText("SWITCH COLOR(RED)");
                    for (PointColor pointColor : pointColors)
                        pointDS.add(new PointD(pointColor.x, pointColor.y_red/ColorAvgUtil.avg_V));
                }
                if (color_status == 1) {
                    btn_changecolor.setText("SWITCH COLOR(BLUE)");
                    for (PointColor pointColor : pointColors)
                        pointDS.add(new PointD(pointColor.x, pointColor.y_blue/ColorAvgUtil.avg_V));
                }
                if (color_status == 2) {
                    btn_changecolor.setText("SWITCH COLOR(GREEN)");
                    for (PointColor pointColor : pointColors)
                        pointDS.add(new PointD(pointColor.x, pointColor.y_green/ColorAvgUtil.avg_V));
                }
                break;

            case R.id.iv_add: {
                PopupMenu popup = new PopupMenu(this, iv_add);

                MenuInflater inflater = popup.getMenuInflater();

                inflater.inflate(R.menu.my_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(this);

                popup.show();
                break;
            }
            case R.id.illumination_cali: {
//                if (Scaletag) {
//                    Scaletag = false;
//                    finish_tag = true;
//                    Toast.makeText(this, "Selecting Scale has turned off!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Scaletag = true;
//                    finish_tag = false;
//                    Scale_i = 0;
//                    ScaleList = new ArrayList<>();
//                    Toast.makeText(this, "Now you can select Scale!", Toast.LENGTH_SHORT).show();
//                }
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
                                        Toast.makeText(CatchrgbActivity.this, "Fail to create image!", Toast.LENGTH_SHORT).show();
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


            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;

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
