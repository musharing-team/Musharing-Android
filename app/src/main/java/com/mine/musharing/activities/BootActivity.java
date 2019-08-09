package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mine.musharing.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <h1>启动活动</h1>
 * 启动app时看到的界面，加载完成后自动跳转到登录活动
 */
public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        // 一秒后进入登录界面
        Timer mTimer = new Timer();
        TimerTask startLoginTimerTask = new TimerTask() {
            @Override
            public void run() {
                toLogin();
            }
        };
        mTimer.schedule(startLoginTimerTask, 1000);
    }

    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void startOnClick(View view) {
        toLogin();
    }
}
