package com.group3.myandroid;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.group3.myandroid.global.EasyLogger;

public class StartActivity extends AppCompatActivity {

    //onCreate...アクディビティが最初に作成されるときに実行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button startButton = findViewById(R.id.StartButton);    //ボタンを参照

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //クリック時にMeasurementActivityのアクティビティを開始
                Intent intent = new Intent(StartActivity.this, MeasurementActivity.class);
                startActivity(intent);
            }
        });
    }


}