package com.reyansh.easytouch.Services;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ram.speed.booster.RAMBooster;
import com.ram.speed.booster.interfaces.CleanListener;
import com.ram.speed.booster.interfaces.ScanListener;
import com.ram.speed.booster.utils.ProcessInfo;
import com.reyansh.easytouch.Activities.LockScreenActivity;
import com.reyansh.easytouch.Activities.MainActivity;
import com.reyansh.easytouch.Activities.ScreenShotActivity;
import com.reyansh.easytouch.Adapters.EasyAppsAdapter;
import com.reyansh.easytouch.BroadcastReceivers.DeviceAdmin;
import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.Models.EasyAppsModel;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.Constants;
import com.reyansh.easytouch.Utils.PreferencesHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.reyansh.easytouch.Utils.Constants.TAG;


/**
 * Created by reyansh on 1/15/18.
 */

public class OverlayService extends Service implements View.OnTouchListener, View.OnClickListener {

    boolean rotationEnabled = false;
    private View mContentView;
    private View mDeleteView;
    private ImageButton mEasyTouchButton;
    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mContentViewParams;
    private WifiManager mWifiManager;
    public boolean isFlashLightOn = false;
    private Common mApp;
    public static int NOTIFICATION_ID = 6;
    private Camera cam;
    private Handler mHandler;
    private RAMBooster mBooster;
    private BluetoothAdapter mBluetoothAdapter;
    private RecyclerView mRecyclerView;
    private EasyAppsAdapter mEasyAppsAdapter;


    public boolean mWifiEnabled;
    public boolean mBluetoothEnabled;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (Common) getApplicationContext();

        mContentView = LayoutInflater.from(this).inflate(R.layout.layout_overlay_view, null);
        mRecyclerView = mContentView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        mEasyAppsAdapter = new EasyAppsAdapter();
        mRecyclerView.setAdapter(mEasyAppsAdapter);

        setPanelBackgroundColor(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.PANEL_COLOR, 000000));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mApp.isServiceRunning = true;
        mApp.setOverLayService(this);

        mBooster = new RAMBooster(getApplicationContext());
        mBooster.setDebug(true);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(mApp, String.valueOf(message.obj), Toast.LENGTH_SHORT).show();
            }
        };
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        if (mWifiManager.isWifiEnabled()) {
            mBluetoothEnabled = true;
        } else {
            mBluetoothEnabled = false;

        }

        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothEnabled = true;
        } else {
            mBluetoothEnabled = false;
        }

        mEasyTouchButton = new ImageButton(this);
        setIcon(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.ICON_DRAWABLE, R.drawable.ic_circle));

        mEasyTouchButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mEasyTouchButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        mContentView.findViewById(R.id.parentPanel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showButton();
            }
        });


        mEasyTouchButton.setOnTouchListener(this);
        mEasyTouchButton.setOnClickListener(this);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 10;
        params.height = 100;
        params.width = 100;
        mWindowManager.addView(mEasyTouchButton, params);

        mContentViewParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        mContentViewParams.gravity = Gravity.CENTER;
        mWindowManager.addView(mContentView, mContentViewParams);
        mContentView.setVisibility(View.GONE);

        mDeleteView = LayoutInflater.from(this).inflate(R.layout.layout_fake_view, null);

        WindowManager.LayoutParams deleteParams = new WindowManager.LayoutParams(
                LayoutParams.MATCH_PARENT,
                350,
                LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        deleteParams.gravity = Gravity.CENTER | Gravity.BOTTOM;

        mWindowManager.addView(mDeleteView, deleteParams);
        mDeleteView.setVisibility(View.GONE);
        startForeground(NOTIFICATION_ID, notification());

        setSize(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.ICON_SIZE, 0) + 100);
        setAlpha(PreferencesHelper.getInstance().getInt(PreferencesHelper.Key.ICON_ALPHA, 155) + 100);
    }

    public void updateData() {
        mEasyAppsAdapter.updateData(mApp.getDBAccessHelper().getEasyApps());
    }


    private void showButton() {
        mContentView.setVisibility(View.GONE);
        mEasyTouchButton.setVisibility(View.VISIBLE);
    }


    private void showView() {
        mContentView.setVisibility(View.VISIBLE);
        mEasyTouchButton.setVisibility(View.GONE);
    }

    private void showBrightness() throws Settings.SettingNotFoundException {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.brightness_dialog, null);

        AlertDialog alertDialog = new AlertDialog.Builder(Common.getInstance()).setView(view)
                .setMessage(R.string.brightness)
                .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        SeekBar mBrightness = view.findViewById(R.id.seekbar);
        mBrightness.setMax(255);

        int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        mBrightness.setProgress(brightness);

        mBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        alertDialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.show();
    }

    public void setScreenBrightness(int brightnessValue) {

        if (brightnessValue >= 0 && brightnessValue <= 255) {
            Settings.System.putInt(getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }
    }


    private void clearRam() {

        if (mBooster == null)
            mBooster = null;
        mBooster = new RAMBooster(this);
        mBooster.setDebug(true);
        mBooster.setScanListener(new ScanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Scan started");
            }

            @Override
            public void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean) {

                Log.d(TAG, String.format(Locale.US, "Scan finished, available RAM: %dMB, total RAM: %dMB", availableRam, totalRam));
                List<String> apps = new ArrayList<String>();
                for (ProcessInfo info : appsToClean) {
                    apps.add(info.getProcessName());
                }
                Log.d(TAG, String.format(Locale.US, "Going to clean founded processes: %s", Arrays.toString(apps.toArray())));
                mBooster.startClean();
            }
        });

        mBooster.setCleanListener(new CleanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Clean started");
            }


            @Override
            public void onFinished(long availableRam, long totalRam) {
                String scannedResult = String.format(Locale.US, "Clean finished, available RAM: %dMB, total RAM: %dMB", availableRam, totalRam);

                Message message = mHandler.obtainMessage(25, scannedResult);
                message.sendToTarget();

                mBooster = null;
            }
        });
        mBooster.startScan(true);
    }


    public void flashLightOn() {

        try {

            cam = Camera.open();
            Camera.Parameters parameters = cam.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(parameters);
            isFlashLightOn = true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff() {
        try {
            cam = Camera.open();
            Camera.Parameters parameters = cam.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(parameters);
            cam.stopPreview();
            cam.release();
            cam = null;
            isFlashLightOn = false;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isAccessibilityServiceEnabled(Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(this, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }


    private Notification notification() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("LAUNCHED_FROM_NOTIFICATION", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


        return new NotificationCompat.Builder(this)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setContentText("DEMO")
                .build();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mEasyTouchButton != null) {
            mWindowManager.removeView(mEasyTouchButton);
            mWindowManager.removeView(mContentView);
            mEasyTouchButton = null;
        }
        mApp.setOverLayService(null);
        mApp.isServiceRunning = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            mEasyTouchButton.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;
            mDeleteView.setVisibility(View.VISIBLE);

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            WindowManager.LayoutParams layoutParams = (LayoutParams) mEasyTouchButton.getLayoutParams();
            float x = event.getRawX();
            float y = event.getRawY();


            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            layoutParams.x = newX;
            layoutParams.y = newY;
            mWindowManager.updateViewLayout(mEasyTouchButton, layoutParams);
            moving = true;
            stop(false);

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            stop(true);

            mDeleteView.setVisibility(View.GONE);
            if (moving) {
                return true;
            }
        }
        return false;
    }

    private void bounceBack() {
        int[] location = new int[2];
        mEasyTouchButton.getLocationOnScreen(location);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int iconWidth = mEasyTouchButton.getWidth();

        if (location[0] + (iconWidth / 2) > screenWidth / 2) {
            overlayAnimation(mEasyTouchButton, location[0], screenWidth - iconWidth);
        } else {
            overlayAnimation(mEasyTouchButton, location[0], 0);
        }
    }

    boolean hapTickPerformed = false;

    private void stop(boolean actionUp) {
        int[] iconLocation = new int[2];

        mEasyTouchButton.getLocationOnScreen(iconLocation);

        int centerX = iconLocation[0] + mEasyTouchButton.getWidth() / 2;
        int centerY = iconLocation[1] + mEasyTouchButton.getHeight() / 2;

        int[] a = new int[2];

        ImageButton deleteButton = mDeleteView.findViewById(R.id.delete);
        deleteButton.getLocationOnScreen(a);

        int delMaxX = a[0] + deleteButton.getWidth();
        int delMaxY = a[1] + deleteButton.getHeight();


        if (centerX > a[0] && centerX < delMaxX && centerY > a[1] && centerY < delMaxY) {
            if (!hapTickPerformed) {
                deleteButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                hapTickPerformed = true;
                deleteButton.setColorFilter(ContextCompat.getColor(this, R.color.red));
            }
            if (actionUp) {
                stopSelf();
            }
        } else {
            hapTickPerformed = false;
            deleteButton.setColorFilter(ContextCompat.getColor(this, R.color.white));
            if (actionUp) bounceBack();
        }
    }


    private void overlayAnimation(final View view2animate, int viewX, int endX) {

        ValueAnimator translateLeft = ValueAnimator.ofInt(viewX, endX);

        translateLeft.setInterpolator(new OvershootInterpolator());
        translateLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                updateViewLayout(view2animate, val);
            }
        });
        translateLeft.setDuration(300);
        translateLeft.start();
    }

    public void updateViewLayout(View view, Integer x) {
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        if (x != null) lp.x = x;
        mWindowManager.updateViewLayout(view, lp);
    }


    @Override
    public void onClick(View v) {
        showView();
    }

    public void setAlpha(int alpha) {
        Log.d(Constants.TAG, "ALPHA " + alpha);
        mEasyTouchButton.setImageAlpha(alpha);
    }

    public void setSize(int size) {
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) mEasyTouchButton.getLayoutParams();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        int[] location = new int[2];
        mEasyTouchButton.getLocationOnScreen(location);

        lp.height = size;
        lp.width = size;

        if (location[0] + (size / 2) > screenWidth / 2) {
            lp.x = screenWidth - size;
        } else {
            lp.x = 0;
        }

        mWindowManager.updateViewLayout(mEasyTouchButton, lp);
    }

    public void setPanelBackgroundColor(int panelBackgroundColor) {
        Drawable drawable1 = mContentView.findViewById(R.id.first_content).getBackground();
        drawable1.setColorFilter(new PorterDuffColorFilter(panelBackgroundColor, PorterDuff.Mode.SRC_ATOP));

        mContentView.findViewById(R.id.first_content).setBackground(drawable1);
    }

    public void setIcon(int resourceId) {
        mEasyTouchButton.setImageResource(resourceId);
    }

    public void performAction(EasyAppsModel easyAppsModel) {
        AudioManager am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        switch (easyAppsModel.mAppName) {
            case "Screenshot":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(Common.getInstance(), ScreenShotActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Common.getInstance().startActivity(intent);
                } else {
                    Toast.makeText(Common.getInstance(), R.string.unable_to_take_screenshot, Toast.LENGTH_SHORT).show();
                }
                showButton();
                break;

            case "Bluetooth":
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothEnabled = false;
                    mBluetoothAdapter.disable();
                } else {
                    mBluetoothEnabled = true;
                    mBluetoothAdapter.enable();
                }
                break;
            case "Brightness":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(getApplicationContext())) {
                        try {
                            showBrightness();
                            showButton();
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                break;
            case "Clean":
                clearRam();
                showButton();
                break;
            case "Flashlight":

                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                    int modifyAudioPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
                    if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(getApplicationContext(), ScreenShotActivity.class);
                        intent.putExtra("FLASH_TORCH", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return;
                    }

                    if (isFlashLightOn) {
                        flashLightOff();
                    } else {
                        flashLightOn();
                    }
                } else {
                    Toast.makeText(mApp, R.string.flash_light_not_available, Toast.LENGTH_SHORT).show();
                }
                break;
            case "Favorites":
                mEasyAppsAdapter.updateData(mApp.getDBAccessHelper().getInstalledApps());
                break;
            case "Home":
                if (isAccessibilityServiceEnabled(AccService.class)) {
                    mApp.getAccessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Common.getInstance())
                            .setTitle(R.string.accessibility_permission)
                            .setMessage(R.string.accessibility_permission_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    mContentView.setVisibility(View.GONE);
                                }
                            }).create();

                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();
                }
                showButton();
                break;
            case "Location":

                Intent startMain = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                showButton();
                break;
            case "Lock":
                DevicePolicyManager dm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                if (dm.isAdminActive(new ComponentName(getApplication(), DeviceAdmin.class))) {
                    dm.lockNow();
                } else {
                    mContentView.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), LockScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                showButton();
                break;
            case "Recent":

                try {
                    Class serviceManagerClass = Class.forName("android.os.ServiceManager");
                    Method getService = serviceManagerClass.getMethod("getService", String.class);
                    IBinder retbinder = (IBinder) getService.invoke(serviceManagerClass, "statusbar");
                    Class statusBarClass = Class.forName(retbinder.getInterfaceDescriptor());
                    Object statusBarObject = statusBarClass.getClasses()[0].getMethod("asInterface", IBinder.class).invoke(null, new Object[]{retbinder});
                    Method clearAll = statusBarClass.getMethod("toggleRecentApps");
                    clearAll.setAccessible(true);
                    clearAll.invoke(statusBarObject);
                    showButton();
                } catch (Exception e) {
                    Toast.makeText(OverlayService.this, R.string.can_not_open_recent_apps, Toast.LENGTH_SHORT).show();
                }
                break;
            case "Auto Rotate":
                if (getRotationScreenFromSettingsIsEnabled()) {
                    rotationEnabled = false;
                    setRotationScreenFromSettings(rotationEnabled);
                } else {
                    rotationEnabled = true;
                    setRotationScreenFromSettings(rotationEnabled);
                }
                showButton();
                break;
            case "Silent":
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(mApp, R.string.silent_mode, Toast.LENGTH_SHORT).show();
                break;
            case "Sound":
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Toast.makeText(mApp, R.string.ringer_mode, Toast.LENGTH_SHORT).show();
                break;
            case "Volume Down":
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                break;
            case "Volume Up":
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                break;
            case "Wifi":
                if (mWifiManager.isWifiEnabled()) {
                    mWifiEnabled = false;
                    mWifiManager.setWifiEnabled(false);


                } else {
                    mWifiManager.setWifiEnabled(true);
                    mWifiEnabled = true;
                }
                break;
            case "Back":
                if (isAccessibilityServiceEnabled(AccService.class)) {
                    mApp.getAccessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Common.getInstance())
                            .setTitle(R.string.accessibility_permission)
                            .setMessage(R.string.accessibility_permission_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    mContentView.setVisibility(View.GONE);
                                }
                            }).create();

                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();
                }
                showButton();
                break;
            case "FAvBack":
                mEasyAppsAdapter.updateData(mApp.getDBAccessHelper().getEasyApps());
                break;
            case "Device":
                mEasyAppsAdapter.updateData(mApp.getDBAccessHelper().getEasyDeviceApps());
                break;

            default:
                if (easyAppsModel.mPackageName != null && !easyAppsModel.mPackageName.equalsIgnoreCase("")) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(easyAppsModel.mPackageName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    showButton();
                }
                break;
        }
        mEasyAppsAdapter.notifyDataSetChanged();
    }

    public boolean getRotationScreenFromSettingsIsEnabled() {

        int result = 0;
        try {
            result = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return result == 1;
    }

    public void setRotationScreenFromSettings(boolean enabled) {
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1) {
                Display defaultDisplay = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, defaultDisplay.getRotation());
                Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            } else {
                Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
            }
            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean isWifiEnabled() {
        return mWifiEnabled;
    }

    public boolean isBluetoothOn() {
        return mBluetoothEnabled;
    }
}