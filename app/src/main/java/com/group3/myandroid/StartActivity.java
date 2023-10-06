package com.group3.myandroid;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.group3.myandroid.global.EasyLogger;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EasyLogger el = new EasyLogger("TestMode",true);

        for (int i = 0; i < 10; i++) {
            el.debug(i + "回目のログ"); // 0 ~ 9まで出力
        }
        el.setGetTrace(false);
        try {
            throw new Exception("エラー発生");
        }catch (Exception e){
            el.error(e); //エラーログを取る
        }
    }


}