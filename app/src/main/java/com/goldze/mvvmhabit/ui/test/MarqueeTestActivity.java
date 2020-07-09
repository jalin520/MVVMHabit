package com.goldze.mvvmhabit.ui.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goldze.mvvmhabit.R;


public class MarqueeTestActivity extends AppCompatActivity {

    EditText editSpeed;
    EditText editText;
    Button btnStop;
    RadioGroup radioGroup_direction;
    EditText editReplaceCount;
    MarqueeTextView marqueeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marquee_test);
        editSpeed = findViewById(R.id.edit_speed);
        editText = findViewById(R.id.edit_text);
        btnStop = findViewById(R.id.btnStop);
        editReplaceCount = findViewById(R.id.edit_replace);
        radioGroup_direction = findViewById(R.id.radioGroup_direction);
        marqueeTextView = findViewById(R.id.marqueeTextView);

        editText.setText(marqueeTextView.getText());
        editSpeed.setText(marqueeTextView.getRndDuration() + "");
        editReplaceCount.setText(marqueeTextView.getRepeatCount() + "");
        marqueeTextView.startScroll();
        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speedStr = editSpeed.getText().toString();
                String replaceStr = editReplaceCount.getText().toString();
                String text = editText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(MarqueeTestActivity.this, "请输入文字", Toast.LENGTH_LONG);
                    return;
                }
                if (TextUtils.isEmpty(speedStr)) {
                    Toast.makeText(MarqueeTestActivity.this, "请输入速度", Toast.LENGTH_LONG);
                    return;
                }
                if (TextUtils.isEmpty(replaceStr)) {
                    Toast.makeText(MarqueeTestActivity.this, "请输入重复次数", Toast.LENGTH_LONG);
                    return;
                }
                int speed = Integer.parseInt(speedStr);
                int replace = Integer.parseInt(replaceStr);
                marqueeTextView.setRndDuration(speed);
                int checkedRadioButtonId = radioGroup_direction.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.radio_auto_horizontal) {
                    marqueeTextView.setDirection(MarqueeTextView.DIRECTION_AUTO_HORIZONTAL);
                } else if (checkedRadioButtonId == R.id.radio_fit_horizontal) {
                    marqueeTextView.setDirection(MarqueeTextView.DIRECTION_FIT_HORIZONTAL);
                } else if (checkedRadioButtonId == R.id.radio_auto_vertical) {
                    marqueeTextView.setDirection(MarqueeTextView.DIRECTION_AUTO_VERTICAL);
                } else if (checkedRadioButtonId == R.id.radio_fit_vertical) {
                    marqueeTextView.setDirection(MarqueeTextView.DIRECTION_FIT_VERTICAL);
                }
                marqueeTextView.setRepeatCount(replace);
                marqueeTextView.setText(text);
                marqueeTextView.startScroll();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marqueeTextView.stopScroll();
            }
        });
    }
}
