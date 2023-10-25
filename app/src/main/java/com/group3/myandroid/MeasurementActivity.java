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

//メモ:ボタンをインポートする

public class MeasurementActivity extends AppCompatActivity {

    private long startTime;
    private Handler handler = new Handler();
    private TextView timerTextView;
    private long elapsedTime = 0;
    private Runnable updateTimeRunnable = new Runnable() {

        @Override
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimeDisplay(elapsedTime);
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Button stopButton = findViewById(R.id.StopButton);    //ストップボタンを参照
        Button pauseButton = findViewById(R.id.PauseButton);    //ポーズボタンを参照

        timerTextView = findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimeRunnable, 0);

        stopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //クリック時にMeasurementActivity
                Intent intent = new Intent(MeasurementActivity.this, CouponManagementActivity.class);
                startActivity(intent);

                handler.removeCallbacks(updateTimeRunnable);

                intent.putExtra("elapsedTime", elapsedTime);
                startActivity(intent);

            }
        });

        final boolean[] isPaused = {false}; // 一時停止状態を管理するフラグ


        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //クリック時に一時停止する機構
                if (isPaused[0]) {
                    // 再開
                    handler.postDelayed(updateTimeRunnable, 0);
                    //pauseButton.setText("PAUSE"); // ボタンのテキストを"Pause"に戻す
                } else {
                    // 一時停止
                    handler.removeCallbacks(updateTimeRunnable);
                    //pauseButton.setText("RESUME"); // ボタンのテキストを"Resume"に変更
                }
                isPaused[0] =!isPaused[0]; // 一時停止状態を切り替える
            }
        });


    }


    private void updateTimeDisplay(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

}