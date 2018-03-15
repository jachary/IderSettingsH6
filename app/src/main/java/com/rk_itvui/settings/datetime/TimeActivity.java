package com.rk_itvui.settings.datetime;

import com.zxy.idersettings.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.format.DateFormat;
import android.widget.TextView;

public class TimeActivity extends Activity {

    private TextView tvTime;
    long sysTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_setting);
        tvTime = (TextView) findViewById(R.id.mytime);
        new TimeThread().start(); 
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 1:
            	sysTime = System.currentTimeMillis();
            	CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
            	tvTime.setText(sysTimeStr); 
            	break;

            }
        }
    };

}
