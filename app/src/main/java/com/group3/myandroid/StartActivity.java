package com.group3.myandroid;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.group3.myandroid.global.EasyLogger;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    //onCreate...アクディビティが最初に作成されるときに実行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.StartButton);    //ボタンを参照

        //タイマーシステム
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, MeasurementActivity.class);
                startActivity(intent);
            }
        };

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //クリック時にMeasurementActivityのアクティビティを開始
                Timer timer = new Timer();
                timer.schedule(task, 3000);
            }
        });
    }


}