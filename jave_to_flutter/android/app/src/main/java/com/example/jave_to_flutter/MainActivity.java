package com.example.jave_to_flutter;

import static android.content.Context.BATTERY_SERVICE;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "samples.flutter.dev/battery";
    private static final long TIMER_INTERVAL = 5000; // Interval in milliseconds (5 seconds)
    private Handler handler;
    private Runnable runnable;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("getBatteryLevel")) {
                                int batteryLevel = getBatteryLevel();
                                if (batteryLevel != -1) {
                                    result.success(batteryLevel);
                                } else {
                                    result.error("UNAVAILABLE", "Battery level not available.", null);
                                }
                            } else if (call.method.equals("startBackgroundTimer")) {
                                startBackgroundTimer();
                                result.success(null);
                            } else if (call.method.equals("stopBackgroundTimer")) {
                                stopBackgroundTimer();
                                result.success(null);
                            }  else if (call.method.equals("getString")) {
                                new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
                                        .invokeMethod("getString", "A");
                                result.success("BB");
                            }else {
                                result.notImplemented();
                            }
                        }
                );
    }

    private void startBackgroundTimer() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                int batteryLevel = getBatteryLevel();
                new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
                        .invokeMethod("updateBatteryLevel", batteryLevel);

                handler.postDelayed(this, TIMER_INTERVAL); // Repeat every TIMER_INTERVAL
            }
        };
        handler.post(runnable); // Start the timer
    }

    private void stopBackgroundTimer() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private int getBatteryLevel() {
        int batteryLevel = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(getApplicationContext()).
                    registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                    intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        return batteryLevel;
    }
}
