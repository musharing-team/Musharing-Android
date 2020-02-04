package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionSet;
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

        // 稍作等待后开始加载数据
        Timer mTimer = new Timer();
        TimerTask loadTimerTask = new TimerTask() {
            @Override
            public void run() {
                load();
            }
        };
        mTimer.schedule(loadTimerTask, 200);

        setupTransition();
    }

    /**
     * 加载数据
     */
    private void load() {
        initSensitiveWordsUtils();

        // 跳转到登录界面
        runOnUiThread(() -> {
            Timer mTimer = new Timer();
            TimerTask startLoginTimerTask = new TimerTask() {
                @Override
                public void run() {
                    toLogin();
                }
            };
            mTimer.schedule(startLoginTimerTask, 20);
        });
    }

    /**
     * 初始化敏感词库
     */
    private void initSensitiveWordsUtils() {
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
    }

    /**
     * 进入登录界面
     */
    private void toLogin() {
        runOnUiThread(() -> {
            Intent intent = new Intent(this, LoginActivity.class);
            Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(BootActivity.this).toBundle();
            startActivity(intent, translateBundle);
            // finish();    // finish() 会与专场动画矛盾
        });
    }

    public void startOnClick(View view) {
        toLogin();
    }

    /**
     * 设置 Activity 的转场动画
     */
    private void setupTransition() {
        TransitionSet transitionSet1 = Utility.getRandomTransitionSet();
        TransitionSet transitionSet2 = Utility.getRandomTransitionSet();
        TransitionSet transitionSet3 = Utility.getRandomTransitionSet();

        getWindow().setEnterTransition(transitionSet1);
        getWindow().setExitTransition(transitionSet2);
        getWindow().setReenterTransition(transitionSet3);
    }
}
