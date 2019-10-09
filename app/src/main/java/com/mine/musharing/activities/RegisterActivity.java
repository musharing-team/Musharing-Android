package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.bases.User;
import com.mine.musharing.requestTasks.RegisterTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.UserUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h1>注册活动</h1>
 * 提供头像、用户名、密码尝试注册，成功后转到LoginActivity
 */
public class RegisterActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private ImageView imageView;

    // TODO(b03) 实现让用户可选则头像
    private String imgUrl = "https://cdn.pixabay.com/photo/2014/12/21/23/59/block-576506_1280.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressBar = findViewById(R.id.register_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        imageView = findViewById(R.id.register_img);
        Glide.with(this).load(imgUrl).into(imageView);
    }

    public void  registerOnClick(View view) {
        EditText userNameText = findViewById(R.id.register_user_name);
        EditText passwordText = findViewById(R.id.register_password);
        EditText passwordAgainText = findViewById(R.id.register_password_again);

        String userName = userNameText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordAgain = passwordAgainText.getText().toString();

        if (TextUtils.isEmpty(imgUrl) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请选择头像并正确输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.equals(password, passwordAgain)) {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        String nameEncoded = UserUtil.encodeName(userName);
        String passwordEncrypted = UserUtil.encryptPassword(nameEncoded, password);

        new RegisterTask(new RequestTaskListener<User>() {
            // TODO [b01](todo://LoginActivity/b01)
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "注册成功，请登录\nWelcome, " + user.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailed(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                progressBar.setVisibility(View.GONE);
            }
        }).execute(nameEncoded, passwordEncrypted, imgUrl);
    }
}
