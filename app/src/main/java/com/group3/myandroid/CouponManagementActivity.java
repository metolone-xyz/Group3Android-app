package com.group3.myandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.group3.myandroid.global.EasyLogger;

public class CouponManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_management);
    }
    EasyLogger el = new EasyLogger("TestMode",true);
}