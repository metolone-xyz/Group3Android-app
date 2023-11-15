package com.group3.myandroid;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.group3.myandroid.global.EasyLogger;

public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int backgroundCount = 0;

    private float prevFilteredValue = 0; //前回のフィルタリング後の値
    private float prevRawValue = 0; //前回の生データの値

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        //通知チャンネルの作成
        String CHANNEL_ID = "StepCounterServiceChannel";
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Step Counter Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

        //通知の作成
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("歩数計測中")
                .setContentText("アプリがバックグラウンドでも歩数を計測しています。")
                // .setSmallIcon(R.drawable.icon) // 適切なアイコンを設定
                .build();

        // サービスをフォアグラウンドで開始
        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // 全体の加速度の計算
            float currentRawValue = (float) Math.sqrt(x * x + y * y + z * z);

            // ローパスフィルタを適用
            float a = 0.5f; // ローパスフィルタ係数
            float filteredValue = a * currentRawValue + (1 - a) * prevFilteredValue;

            EasyLogger el = new EasyLogger("BackgroundValue", true);

            // 極大値の検出
            if (filteredValue < prevFilteredValue && prevFilteredValue > prevRawValue
                    && 1.0f < prevFilteredValue - prevRawValue
                    && prevFilteredValue - prevRawValue < 3.0f) {
                backgroundCount++;

                el.debug("加速度の値の差" + (prevFilteredValue - prevRawValue));
            }

            // 値の更新
            prevRawValue = currentRawValue;
            prevFilteredValue = filteredValue;

            // 歩数更新のブロードキャスト送信
            Intent intent = new Intent("com.group3.myandroid.STEP_COUNT_UPDATE");
            intent.putExtra("backgroundCount", backgroundCount);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 必要に応じて実装
    }
}
