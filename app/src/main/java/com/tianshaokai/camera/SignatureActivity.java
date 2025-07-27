package com.tianshaokai.camera;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignatureActivity extends AppCompatActivity {
    private SignatureView signatureView;
    private TextView btnColorBlack, btnColorRed, btnColorBlue;
    private TextView btnStrokeWidth, btnUndo, btnClear;
    private float currentStrokeWidth = 5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        initViews();
        setupListeners();
    }

    private void initViews() {
        signatureView = findViewById(R.id.signature_view);
        btnColorBlack = findViewById(R.id.btn_color_black);
        btnColorRed = findViewById(R.id.btn_color_red);
        btnColorBlue = findViewById(R.id.btn_color_blue);
        btnStrokeWidth = findViewById(R.id.btn_stroke_width);
        btnUndo = findViewById(R.id.btn_undo);
        btnClear = findViewById(R.id.btn_clear);
    }

    private void setupListeners() {
        // 颜色选择按钮
        btnColorBlack.setOnClickListener(v -> signatureView.setColor(Color.BLACK));
        btnColorRed.setOnClickListener(v -> signatureView.setColor(Color.RED));
        btnColorBlue.setOnClickListener(v -> signatureView.setColor(Color.BLUE));

        // 画笔粗细按钮
        btnStrokeWidth.setOnClickListener(v -> showStrokeWidthDialog());

        // 撤销按钮
        btnUndo.setOnClickListener(v -> signatureView.undo());

        // 清空按钮
        btnClear.setOnClickListener(v -> showClearConfirmDialog());
    }

    private void showStrokeWidthDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_stroke_width, null);
        SeekBar seekBar = dialogView.findViewById(R.id.seek_bar);
        TextView tvWidth = dialogView.findViewById(R.id.tv_width);

        seekBar.setProgress((int) (currentStrokeWidth - 1));
        tvWidth.setText(String.format("%.1f", currentStrokeWidth));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentStrokeWidth = progress + 1;
                tvWidth.setText(String.format("%.1f", currentStrokeWidth));
                signatureView.setStrokeWidth(currentStrokeWidth);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new AlertDialog.Builder(this)
                .setTitle("画笔粗细")
                .setView(dialogView)
                .setPositiveButton("确定", null)
                .show();
    }

    private void showClearConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("清空画布")
                .setMessage("确定要清空画布吗？")
                .setPositiveButton("确定", (dialog, which) -> signatureView.clear())
                .setNegativeButton("取消", null)
                .show();
    }
}
