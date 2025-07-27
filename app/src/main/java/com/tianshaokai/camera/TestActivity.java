package com.tianshaokai.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        TextView textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });

//        QuestionStateIndicatorView largeIndicator = findViewById(R.id.indicator);
//        largeIndicator.setQuestionNumber(2);    // 设置题目编号
//        largeIndicator.setIsSelected(true);     // 设置选中状态
//        largeIndicator.setIsCorrect(true);      // 设置是否正确
    }




    private void jump() {
        String scheme = "tal://onlineclass/universal_video?knowledge_id=35002548&video_id=spjjfc2f7a6bb4f0038ebbb5cb2f428b11e4&video_title=图案设计&question_ids_json=[{\"knowledge_id\":\"35002548\",\"question_id\":\"6b80709326b84d5082443de06d54d02f\"},{\"knowledge_id\":\"35002548\",\"question_id\":\"fb33a8bf4e51490db72d1e375483766c\"},{\"knowledge_id\":\"35002548\",\"question_id\":\"25c44e8ad7704df49b109bc91e74713b\"}]&business_id=&practice_pre_title=&practice_finish_tip=&pkg_name=&pkg_action=&enable_user_track=1&studyapp=zypg&user_track_page_name=作业批改&business_source_type=1&moduleName=图案设计&subjectId=2&extra={\"kp_id\":\"35002548\",\"request_id\":\"5e68f12f0a2ecdeedaea4c4b0f232997\",\"image_id\":\"27f8d589-6c5e-40e0-a04b-190bbd24c749\",\"uuid\":\"0f101047-8fc1-489b-8b3e-3164b98b7753\"}\"";


        Intent intent = new Intent();
        intent.setData(Uri.parse(scheme));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
