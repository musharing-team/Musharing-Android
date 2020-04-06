package com.mine.musharing.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mine.musharing.R;
import com.mine.musharing.utils.SensitiveWordsUtils;
import com.mine.musharing.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h1>启动活动</h1>
 * 启动app时看到的界面，加载完成后自动跳转到登录活动
 */
public class BootActivity extends AppCompatActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

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

        String isFirstEnterApp = pref.getString("LastAppVersion", "NEVER_USED");

        if (!Objects.equals(isFirstEnterApp, this.getString(R.string.version))) {
            startPrivacyDialog();
        } else {
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
     * 隐私弹窗实现说明
     * <p>
     * 参考：https://blog.csdn.net/Snow_Ice_Yang/article/details/103637642
     */
    private void startPrivacyDialog() {
        runOnUiThread(() -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.show();
            alertDialog.setCancelable(false);
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.dialog_initmate);
                window.setGravity(Gravity.CENTER);

                TextView tvContent = window.findViewById(R.id.tv_content);
                TextView tvCancel = window.findViewById(R.id.tv_cancel);
                TextView tvAgree = window.findViewById(R.id.tv_agree);
                String str = "感谢您对Musharing项目的支持!我们非常重视您的个人信息和隐私保护。\n" +
                        "为了更好地保障您的个人权益，在您使用我们的产品前，" +
                        "请务必审慎阅读《隐私政策》和《用户协议》内的所有条款，" +
                        "尤其是:\n" +
                        " 1.我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，以及您的用户权利等条款;\n" +
                        " 2.约定我们的限制责任、免责条款;\n" +
                        " 3.其他以颜色或加粗进行标识的重要条款。\n" +
                        "如您对以上协议有任何疑问，" +
                        "可通过发送邮件至musharing@163.com与我们联系。" +
                        "您点击“同意并继续”的行为即表示您已阅读完毕并同意以上协议的全部内容。" +
                        "如您同意以上协议内容，请点击“同意并继续”，开始使用我们的产品和服务!";

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(str);
                final int start = str.indexOf("《");//第一个出现的位置
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // 隐私政策
                        Intent intent = new Intent(BootActivity.this, PrivacyActivity.class);
                        Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(BootActivity.this).toBundle();
                        startActivity(intent, translateBundle);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.tpsl2Blue));
                        ds.setUnderlineText(false);
                    }
                }, start, start + 6, 0);

                int end = str.lastIndexOf("《");
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // 用户协议
                        Intent intent = new Intent(BootActivity.this, TaCActivity.class);
                        Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(BootActivity.this).toBundle();
                        startActivity(intent, translateBundle);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.tpsl2Blue));
                        ds.setUnderlineText(false);
                    }
                }, end, end + 6, 0);

                tvContent.setMovementMethod(LinkMovementMethod.getInstance());
                tvContent.setText(ssb, TextView.BufferType.SPANNABLE);


                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences.Editor editor = pref.edit();
                        editor.putString("LastAppVersion", "NEVER_USED");
                        editor.apply();
                        alertDialog.cancel();
                        finish();
                    }
                });

                tvAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences.Editor editor = pref.edit();
                        editor.putString("LastAppVersion", getResources().getString(R.string.version));
                        editor.apply();
                        alertDialog.cancel();
                        toLogin();
                    }
                });
            }

        });
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
