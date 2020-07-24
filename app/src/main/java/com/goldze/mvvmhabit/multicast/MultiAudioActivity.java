package com.goldze.mvvmhabit.multicast;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.goldze.mvvmhabit.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.goldze.mvvmhabit.R.layout.activity_multi_audio;

/**
 * 多点广播，局域网内通过广播方式传输数据
 */
public class MultiAudioActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSend;
    Button btnReceive;
    Button btnClearLog;
    TextView tvTip;
    MultiSendThread multiSendThread;
    MultiReceiveThread multiReceiveThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_multi_audio);
        btnSend = findViewById(R.id.btn_start_send);
        btnReceive = findViewById(R.id.btn_start_receive);
        btnClearLog = findViewById(R.id.btn_clear_log);
        tvTip = findViewById(R.id.tip);
        btnSend.setOnClickListener(this);
        btnReceive.setOnClickListener(this);
        btnClearLog.setOnClickListener(this);
        openAudioPermission();
    }

    public void openAudioPermission() {
        //请求打开相机权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            ToastUtils.showShort("录音权限已打开");
                        } else {
                            ToastUtils.showShort("权限被拒绝");
                        }
                    }
                });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_send:
                btnSend.setVisibility(View.GONE);
                btnReceive.setVisibility(View.GONE);
                tvTip.append("正在发送...");
                multiSendThread = new MultiSendThread();
                multiSendThread.start();
                multiSendThread.setOnSendListener(new MultiSendThread.OnSendListener() {
                    @Override
                    public void onSend(final String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTip.append(message);
                            }
                        });
                    }
                });
                break;
            case R.id.btn_start_receive:
                btnSend.setVisibility(View.GONE);
                btnReceive.setVisibility(View.GONE);
                tvTip.append("正在接收...");
                multiReceiveThread = new MultiReceiveThread();
                multiReceiveThread.start();
                multiReceiveThread.setOnReceiveListener(new MultiReceiveThread.OnReceiveListener() {
                    @Override
                    public void onReceive(final String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvTip.append(message);
                            }
                        });
                    }
                });
                break;
            case R.id.btn_clear_log:
                tvTip.setText("");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (multiSendThread != null) {
            multiSendThread.interrupt();
            multiSendThread.release();
        }
        if (multiReceiveThread != null) {
            multiReceiveThread.interrupt();
            multiReceiveThread.release();
        }
        super.onDestroy();
    }
}
