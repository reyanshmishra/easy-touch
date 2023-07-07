package com.reyansh.easytouch.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.OrientationEventListener;
import android.widget.Toast;

import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class ScreenShotActivity extends Activity {

    private MediaProjection sMediaProjection;
    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private OrientationChangeCallback mOrientationChangeCallback;

    private int mWidth;
    private int mHeight;
    private int mRotation;
    private int mNumberOfImages = 0;

    private Common mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (Common) getApplicationContext();
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        if (getIntent().hasExtra("FLASH_TORCH")) {
            if (checkAndRequestPermissions(Manifest.permission.CAMERA)) {
                if (mApp.getOverLayService() != null) {
                    mApp.getOverLayService().flashLightOn();
                    finish();
                }
            }
        } else {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    mHandler = new Handler();
                    Looper.loop();
                }
            }.start();
            startProjection();
        }
    }


    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    if (mVirtualDisplay != null)
                        mVirtualDisplay.release();

                    if (mImageReader != null)
                        mImageReader.setOnImageAvailableListener(null, null);
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null)
                        mVirtualDisplay.release();
                    if (mImageReader != null)
                        mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null)
                        mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.SCREENSHOT_REQUEST && resultCode == Activity.RESULT_OK) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            if (sMediaProjection != null) {
                mDisplay = getWindowManager().getDefaultDisplay();
                createVirtualDisplay();
                mOrientationChangeCallback = new OrientationChangeCallback(this);

                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }
                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        } else {
            finish();
        }
    }


    /**
     * Permission check if version is >= 6 (Marshmallow.)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (Constants.REQUEST_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startProjection();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.grant_permission);
                builder.setMessage(R.string.grant_permission_message);
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });


                builder.setPositiveButton(R.string.open_settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                builder.create().show();
            }
        } else if (requestCode == Constants.CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mApp.getOverLayService() != null) {
                    mApp.getOverLayService().flashLightOn();
                    finish();
                }
            }
        }
    }

    private void startProjection() {
        if (!Common.isKitkat()) {
            if (checkAndRequestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), Constants.SCREENSHOT_REQUEST);
            }
        } else {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), Constants.SCREENSHOT_REQUEST);
        }
    }

    private boolean checkAndRequestPermissions(String permission) {
        int modifyAudioPermission = ContextCompat.checkSelfPermission(this, permission);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(permission);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            if (permission.equalsIgnoreCase(Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.CAMERA_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.REQUEST_PERMISSIONS);
            }
            return false;
        }
        return true;
    }


    private void stopProjection() {
        mHandler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
    }

    private void createVirtualDisplay() {
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay("EASY_TOUCH_SS",
                mWidth,
                mHeight,
                getResources().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mImageReader.getSurface(),
                null,
                mHandler);

        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }


    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    mNumberOfImages++;

                    if (mNumberOfImages == 2) {
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight);
                        String path = saveBitmap(bitmap);
                        Toast.makeText(ScreenShotActivity.this, getResources().getString(R.string.screenshot_saved) + " " + path, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                Toast.makeText(ScreenShotActivity.this, R.string.unable_to_take_screenshot, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
                stopProjection();
                finish();
            }
        }
    }

    Bitmap cropBitmapTransparency(Bitmap sourceBitmap) {
        int minX = sourceBitmap.getWidth();
        int minY = sourceBitmap.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < sourceBitmap.getHeight(); y++) {
            for (int x = 0; x < sourceBitmap.getWidth(); x++) {
                int alpha = (sourceBitmap.getPixel(x, y) >> 24) & 255;
                if (alpha > 0)   // pixel is not 100% transparent
                {
                    if (x < minX)
                        minX = x;
                    if (x > maxX)
                        maxX = x;
                    if (y < minY)
                        minY = y;
                    if (y > maxY)
                        maxY = y;
                }
            }
        }
        if ((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(sourceBitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    private String saveBitmap(Bitmap bitmap) {

        try {
            String path = Environment.getExternalStorageDirectory() + File.separator + "EasyTouch";
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdir();
            }

            OutputStream fOut = null;
            File file = new File(path, "/EasyTouch" + System.currentTimeMillis() + ".png");
            if (!file.exists()) {
                file.createNewFile();
            }
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            bitmap.recycle();
            return file.getAbsolutePath();

        } catch (Exception e) {
            Toast.makeText(this, R.string.unable_to_take_screenshot, Toast.LENGTH_SHORT).show();
            return "" + e.getMessage();
        }
    }
}