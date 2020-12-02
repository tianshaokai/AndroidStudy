package com.tianshaokai.study;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tianshaokai.common.utils.preference.Preferences;

import java.util.Map;

public class CacheActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_common);

        textView = findViewById(R.id.text);

        Preferences preferences = Preferences.build(this);

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                preferences.putString("string " + i, String.valueOf(System.currentTimeMillis()));
            } else if (i % 3 == 0) {
                preferences.putLong("long " + i, System.currentTimeMillis());
            }
        }


        StringBuilder stringBuffer = new StringBuilder();

        Map<String, ?> map = preferences.getAll();
        for (Map.Entry<String, ?> m : map.entrySet()) {
            if ("double".equals(m.getKey()) || "password".equals(m.getKey())) {
                stringBuffer.append(m.getKey()).append(": ").append(preferences.getDouble(m.getKey(), 0)).append("\n\n");
            } else {
                stringBuffer.append(m.getKey()).append(": ").append(m.getValue()).append("\n\n");
            }
        }
        Log.d("CacheActivity", stringBuffer.toString());
        textView.setText(stringBuffer.toString());
    }
}
