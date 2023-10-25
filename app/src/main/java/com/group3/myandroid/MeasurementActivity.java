package com.group3.myandroid;

import android.content.Intent;
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

public class MeasurementActivity extends AppCompatActivity {

    private long startTime;
    private Handler handler = new Handler();
    private TextView timerTextView;
    private long elapsedTime = 0;

    //歩数カウント関係の変数
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount = 0;

    private Runnable updateTimeRunnable = new Runnable() {

        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimeDisplay(elapsedTime);
            handler.postDelayed(this, 1000);
        }
    };

    //歩数センサーのリスナー
    private final SensorEventListener stepListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                //UIを更新する処理
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //とりあえず無視
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Button startButton = findViewById(R.id.StopButton);    //ボタンを参照

        timerTextView = findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimeRunnable, 0);

        //センサーを初期化
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(stepSensor == null){
            //エラー処理
        }

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //クリック時にCouponMeasurementActivityに移動
                Intent intent = new Intent(MeasurementActivity.this, CouponManagementActivity.class);
                startActivity(intent);

                handler.removeCallbacks(updateTimeRunnable);

                intent.putExtra("elapsedTime", elapsedTime);
                startActivity(intent);

            }
        });

    }

    //アクティビティがユーザーとの対話に戻る直前に呼ばれる。アクティブな状態になるとすぐにセンサーのデータを受け取る
    @Override
    protected void onResume() {
        super.onResume();
        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    //アクティビティが全面から離れる直前に呼び出される。
    @Override
    protected void onPause() {
        super.onPause();
        if (stepSensor != null) {
            sensorManager.unregisterListener(stepListener);
        }
    }

    //時間を更新するためのメソッド
    private void updateTimeDisplay(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    //歩数を更新するためのメソッド
    private void updateStepCount(){
        TextView stepCountTextView = findViewById(R.id.stepCountTextView); //表示するUI
        stepCountTextView.setText("Steps: " + stepCount);   //表示内容
    }

}