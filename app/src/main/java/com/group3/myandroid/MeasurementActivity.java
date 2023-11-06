
package com.group3.myandroid;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.group3.myandroid.global.EasyLogger;
import org.w3c.dom.Text;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//メモ:ボタンをインポートする

public class MeasurementActivity extends AppCompatActivity implements SensorEventListener{

    private long startTime;
    private Handler handler = new Handler();
    private TextView timerTextView;
    private long elapsedTime = 0;

    private long pausedTime = 0;  // 追加: 一時停止時の経過時間を保存する変数
    private boolean isPaused = false;  // 追加: 一時停止の状態を追跡する変数

    private SensorManager sensorManager; //歩数を管理するための変数
    private Sensor stepSensor;
    private int stepCount = 0;
    private TextView measurementStepCountTextView;

    private float lastAccel = 0f;
    private float accel = 0f;
    private final float threshold = 12f;  // 歩数をカウントするための加速度のしきい値

    private Runnable updateTimeRunnable = new Runnable() {

        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimeDisplay(elapsedTime);
            handler.postDelayed(this, 1000);
        }
    };

    //private boolean isPaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Button stopButton = findViewById(R.id.StopButton);    //ストップボタンを参照
        Button pauseButton = findViewById(R.id.PauseButton);    //ポーズボタンを参照


        timerTextView = findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimeRunnable, 0);

        //センサーマネージャーの初期化
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度センサーの初期化
        measurementStepCountTextView = findViewById(R.id.measurementStepCountTextView);


        EasyLogger el1 = new EasyLogger("sensorOn",true);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null) {
            el1.debug("This device does not support step detection");
        }

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //クリック時にMeasurementActivity
                Intent intent = new Intent(MeasurementActivity.this, CouponManagementActivity.class);
                startActivity(intent);

                handler.removeCallbacks(updateTimeRunnable);

                intent.putExtra("elapsedTime", elapsedTime);
                intent.putExtra("stepCount", stepCount);
                startActivity(intent);



            }
        });

        //final boolean[] isPaused = {false}; // 一時停止状態を管理するフラグ

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //クリック時に一時停止する機構
                if (isPaused) {
                    // 再開のロジック
                    startTime = System.currentTimeMillis() - pausedTime;
                    handler.postDelayed(updateTimeRunnable, 0);
                    isPaused = false;
                    pauseButton.setText("PAUSE"); // ボタンのテキストを"Pause"に戻す
                } else {
                    // 一時停止のロジック
                    handler.removeCallbacks(updateTimeRunnable);
                    pausedTime = elapsedTime;
                    isPaused = true;
                    pauseButton.setText("RESUME"); // ボタンのテキストを"Resume"に変更
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }



    private void updateTimeDisplay(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lastAccel = accel;
            accel = (float) Math.sqrt(x * x + y * y + z * z);

            float delta = accel - lastAccel;

            if (delta > threshold) {
                stepCount++;
                measurementStepCountTextView.setText("Steps: " + stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 必要に応じて実装
    }


}