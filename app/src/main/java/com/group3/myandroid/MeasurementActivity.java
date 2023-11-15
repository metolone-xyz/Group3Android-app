
package com.group3.myandroid;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.group3.myandroid.global.EasyLogger;

//メモ:ローパスフィルタの係数をいじって適切な値にする
public class MeasurementActivity extends AppCompatActivity implements SensorEventListener{

    private long startTime;
    private final Handler handler = new Handler();
    private TextView timerTextView;
    private long elapsedTime = 0;

    private long pausedTime = 0;  // 一時停止時の経過時間を保存する変数
    private boolean isPaused = false;  // 一時停止の状態を追跡する変数

    private SensorManager sensorManager; //歩数を管理するための変数
    private Sensor stepSensor;
    private int stepCount = 0;
    private TextView measurementStepCountTextView;


    private float prevFilteredValue = 0; //前回のフィルタリング後の値
    private float prevRawValue = 0; //前回の生データの値

    //ブロードキャストレシーバーの宣言
    private BroadcastReceiver stepCountReceiver;

    private final Runnable updateTimeRunnable = new Runnable() {

        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimeDisplay(elapsedTime);
            handler.postDelayed(this, 1000);
        }
    };


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Button stopButton = findViewById(R.id.StopButton);    //ストップボタンを参照
        Button pauseButton = findViewById(R.id.PauseButton);    //ポーズボタンを参照


        timerTextView = findViewById(R.id.timerTextView);   //時間を表示するテキストを参照
        startTime = System.currentTimeMillis(); //
        handler.postDelayed(updateTimeRunnable, 0);

        //センサーマネージャーの初期化
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度センサーの初期化
        measurementStepCountTextView = findViewById(R.id.measurementStepCountTextView);//歩数を表示するテキストの参照


        //歩数センサーがあるかどうかのデバッグ
        EasyLogger el1 = new EasyLogger("sensorOn",true);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null) {
            el1.debug("This device does not support step detection");
        }

        stopButton.setOnClickListener(view -> {
            //クリック時にMeasurementActivity
            Intent intent = new Intent(MeasurementActivity.this, CouponManagementActivity.class);
            startActivity(intent);

            handler.removeCallbacks(updateTimeRunnable);

            intent.putExtra("elapsedTime", elapsedTime);
            intent.putExtra("stepCount", stepCount);
            startActivity(intent);



        });


        pauseButton.setOnClickListener(view -> {

            //クリック時に一時停止する機構
            if (isPaused) {
                //センサーリスナーを再登録して計測を再開
                sensorManager.registerListener(MeasurementActivity.this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);

                // 再開のロジック
                startTime = System.currentTimeMillis() - pausedTime;
                handler.postDelayed(updateTimeRunnable, 0);
                isPaused = false;
                pauseButton.setText(R.string.pause); // ボタンのテキストを"Pause"に戻す

                //サービスを開始する
                Intent intent = new Intent(this, StepCounterService.class);
                stopService(intent);


            } else {
                //センサーのリスナーの登録を解除して計測を一時停止
                sensorManager.unregisterListener(MeasurementActivity.this);

                // 一時停止のロジック
                handler.removeCallbacks(updateTimeRunnable);
                pausedTime = elapsedTime;
                isPaused = true;
                pauseButton.setText(R.string.resume); // ボタンのテキストを"Resume"に変更
            }
        });

        //stepCountReceiverの初期化と登録
        stepCountReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.group3.myandroid.STEP_COUNT_UPDATE")) {
                    int updatedStepCount = intent.getIntExtra("backgroundCount", 0);
                    // UIを更新
                    measurementStepCountTextView.setText("Steps: " + updatedStepCount);
                }
            }
        };
        IntentFilter filter = new IntentFilter("com.group3.myandroid.STEP_COUNT_UPDATE");
        registerReceiver(stepCountReceiver, filter);

    }


    //onResume と　onPauseメソッドをオーバーライドしてリスナーを登録・解除
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Intent intent = new Intent(this, StepCounterService.class);
        stopService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Intent intent = new Intent(this, StepCounterService.class);
        startService(intent);
    }



    @SuppressLint("DefaultLocale")
    private void updateTimeDisplay(long millis) {
        CouponManagementActivity.time(millis, timerTextView);
    }

    @SuppressLint("SetTextI18n")

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            //全体の加速度の計算
            float currentRawValue = (float) Math.sqrt(x*x + y*y + z*z);

            //ローパスフィルタを適用
            //ローパスフィルタ係数
            float a = 0.5f;
            float filteredValue = a *currentRawValue + (1 - a)*prevFilteredValue;

            EasyLogger el = new EasyLogger("SensorValue", true);

            //prevFilteredValue - prevRawValue < 3.0f

            //極大値の検出
            if (filteredValue < prevFilteredValue && prevFilteredValue > prevRawValue && 1.0f< prevFilteredValue - prevRawValue && prevFilteredValue - prevRawValue < 3.0f){
                stepCount++;
                measurementStepCountTextView.setText("Steps: " + stepCount);

                el.debug("加速度の値の差" + (prevFilteredValue - prevRawValue));

            }

            //値の更新
            prevRawValue = currentRawValue;
            prevFilteredValue = filteredValue;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 必要に応じて実装
    }


}