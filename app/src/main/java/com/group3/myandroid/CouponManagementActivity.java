package com.group3.myandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CouponManagementActivity extends AppCompatActivity {

    private TextView timeDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_management);

        timeDisplayTextView = findViewById(R.id.timeDisplayTextView); // Assuming the TextView's ID is timeDisplayTextView
        TextView stepCountTextView = findViewById(R.id.stepCountTextView);
        TextView previousCountTextView = findViewById(R.id.previousCountTextView);
        long passedTime = getIntent().getLongExtra("elapsedTime", 0);
        updateTimeDisplay(passedTime);

        Button backButton = findViewById(R.id.Backbutton);    //ボタンを参照

        int receivedStepCount = getIntent().getIntExtra("stepCount", 0);
        stepCountTextView.setText("今回の記録: " + receivedStepCount);

        int receivedPreviousCount = getIntent().getIntExtra("previousCount", 0);
        previousCountTextView.setText("前回の記録:" + receivedPreviousCount + "歩");

        backButton.setOnClickListener(view -> {
            //クリック時にStartActivity
            Intent intent = new Intent(CouponManagementActivity.this, StartActivity.class);
            intent.putExtra("previousCount", receivedStepCount);
            startActivity(intent);
        });

    }



    private void updateTimeDisplay(long millis) {
        time(millis, timeDisplayTextView);
    }

    static void time(long millis, TextView timeDisplayTextView) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        timeDisplayTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

}