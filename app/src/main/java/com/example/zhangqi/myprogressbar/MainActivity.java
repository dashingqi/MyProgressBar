package com.example.zhangqi.myprogressbar;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MyProgressBar myProgressBar;
    private static final int MSG_UPDATE = 0xfff ;


    private  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int progress = myProgressBar.getProgress();
                myProgressBar.setProgress(++progress);
                if (progress>=100){
                    mHandler.removeMessages(MSG_UPDATE);
                }

                mHandler.sendEmptyMessageDelayed(MSG_UPDATE,100);

            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    private void initView() {
        myProgressBar = findViewById(R.id.myProgressBar);
    }
}
