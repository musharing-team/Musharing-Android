package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mine.musharing.R;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.utils.SensitiveWordsUtils;
import com.mine.musharing.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
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

        // 初始化敏感词库
        Set<String> sensitiveWords = new HashSet<>();

        InputStream in = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            in = getResources().openRawResource(R.raw.sensitive_words);
            inputStreamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedReader.readLine();  // 第一行是注释，跳过
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                sensitiveWords.add(Utility.stringTrim(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        SensitiveWordsUtils.init(sensitiveWords);

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

    /**
     * 进入登录界面
     */
    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void startOnClick(View view) {
        toLogin();
    }
}
