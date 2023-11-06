package com.group3.myandroid;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class CouponManagementActivity extends AppCompatActivity {

    private TextView timeDisplayTextView;

    private TextView stepCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_management);

        timeDisplayTextView = findViewById(R.id.timeDisplayTextView); // Assuming the TextView's ID is timeDisplayTextView
        stepCountTextView = findViewById(R.id.stepCountTextView);
        long passedTime = getIntent().getLongExtra("elapsedTime", 0);
        updateTimeDisplay(passedTime);

        Button backButton = findViewById(R.id.Backbutton);    //ボタンを参照

        int receivedStepCount = getIntent().getIntExtra("stepCount", 0);
        stepCountTextView.setText("Steps: " + receivedStepCount);

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //クリック時にStartActivity
                Intent intent = new Intent(CouponManagementActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

    }



    private void updateTimeDisplay(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        timeDisplayTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        //なんあななあ
    }

}