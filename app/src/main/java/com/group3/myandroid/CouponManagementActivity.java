package com.group3.myandroid;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class CouponManagementActivity extends AppCompatActivity {

    private TextView timeDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_management);

        timeDisplayTextView = findViewById(R.id.timeDisplayTextView); // Assuming the TextView's ID is timeDisplayTextView
        long passedTime = getIntent().getLongExtra("elapsedTime", 0);
        updateTimeDisplay(passedTime);

    }

    private void updateTimeDisplay(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        timeDisplayTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

        //なんあななあ
    }

}