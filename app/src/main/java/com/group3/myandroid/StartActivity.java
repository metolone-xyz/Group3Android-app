package com.group3.myandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    //onCreate...アクディビティが最初に作成されるときに実行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.StartButton); //ボタンを参照
        startButton.setOnClickListener(v -> {
            // 3秒間のカウントダウンを開始
            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // 残り時間を秒単位でtitleTextに表示
                    TextView titleText = findViewById(R.id.titleText);
                    titleText.setText(String.valueOf((millisUntilFinished + 999) / 1000));
                }

                public void onFinish() {
                    // カウントダウンが終了したら、MeasurementActivityに遷移
                    Intent intent = new Intent(StartActivity.this, MeasurementActivity.class);
                    startActivity(intent);

                    //サービスを開始する
                    Intent startIntent = new Intent(StartActivity.this, StepCounterService.class);
                    startService(intent);

                }
            }.start();
        });

    }


}