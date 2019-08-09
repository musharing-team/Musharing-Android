package com.mine.musharing.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mine.musharing.MainActivity;
import com.mine.musharing.R;
import com.mine.musharing.bases.User;
import com.mine.musharing.requestTasks.LoginTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.UserUtil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <h1>登录活动</h1>
 * 输入用户名、密码尝试登录，成功后转到RoomPlaylistActivity; 或 忘记密码/快速注册
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        // TODO(b02) 实现忘记密码、记住密码
    }

    public void loginOnClick(View view) {
        EditText userNameText = findViewById(R.id.login_user_name);
        EditText passwordText = findViewById(R.id.login_password);

        String userName = userNameText.getText().toString();
        String password = passwordText.getText().toString();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        String nameEncoded = UserUtil.encodeName(userName);
        String passwordEncrypted = UserUtil.encryptPassword(nameEncoded, password);

        new LoginTask(new RequestTaskListener<User>() {
            /*
            # TODO(b01): 一个关于提升用户体验的试探性意见

            在layout中顶部添加一条初始时不可见的横幅
            在请求完毕时，如果onFinish参数时FAILED则左侧一个❎（SUCCESS则为✅）
            然后 onSuccess | onFailed 的提示写在 ✅ | ❎ 右边
             */
            @Override
            public void onStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Hello, " + user.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, RoomPlaylistActivity.class);     // TODO(a01) 打开正确的Activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailed(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {
                progressBar.setVisibility(View.GONE);
            }
        }).execute(nameEncoded, passwordEncrypted);

    }

    public void toRegisterOnClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
