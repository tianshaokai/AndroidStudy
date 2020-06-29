package com.tianshaokai.study;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tianshaokai.study.record.AudioRecordActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, AudioRecordActivity.class);
        startActivity(intent);

        finish();


//        Preferences preferences = Preferences.build(this);
//
//        StringBuilder stringBuffer = new StringBuilder();
//
//        Map<String, ?> map = preferences.getAll();
//        for (Map.Entry<String, ?> m : map.entrySet()) {
//            if ("double".equals(m.getKey()) || "password".equals(m.getKey())) {
//                stringBuffer.append(m.getKey()).append(": ").append(preferences.getDouble(m.getKey(), 0)).append("\n\n");
//            } else {
//                stringBuffer.append(m.getKey()).append(": ").append(m.getValue()).append("\n\n");
//            }
//        }
//        text.setText(stringBuffer.toString());
    }
}
