package com.mine.musharing.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mine.musharing.R;
import com.mine.musharing.audio.HotLineRecorder;
import com.mine.musharing.audio.MusicListHolder;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.audio.PlaylistPlayer;
import com.mine.musharing.bases.User;
import com.mine.musharing.requestTasks.LoginTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.UserUtil;

import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mine.musharing.utils.AESUtil;
import java.util.UUID;

/**
 * <h1>登录活动</h1>
 * 输入用户名、密码尝试登录，成功后转到RoomPlaylistActivity; 或 忘记密码/快速注册
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar progressBar;
    private SharedPreferences pref;

    private EditText userNameText;
    private EditText passwordText;
    private CheckBox rememberAccountCheckBox;
    private ImageButton loginButton;

    private String szImei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        rememberAccountCheckBox = findViewById(R.id.remember_account);
        userNameText = findViewById(R.id.login_user_name);
        passwordText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // 引入imei加密后产生密匙
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        szImei = deviceUuid.toString();

        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        getRememberedAccount();
    }

    /**
     * 尝试登录
     */
    public void loginOnClick(View view) {

        String userName = userNameText.getText().toString();
        String password = passwordText.getText().toString();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        rememberAccount();

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
                loginButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(User user) {

                PlayAsyncer.getInstance().setUser(user);
                MusicListHolder.getInstance().setUser(user);
                HotLineRecorder.getInstance().setUser(user);

                runOnUiThread(() -> {
//                    Toast.makeText(LoginActivity.this, "Hello, " + user.getName(), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, RoomPlaylistActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("user", user);
//                    intent.putExtra("data", bundle);
//                    startActivity(intent);
//                    finish();
                    Intent intent = new Intent(LoginActivity.this, MusicChatActivity.class);
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
                loginButton.setVisibility(View.VISIBLE);
            }
        }).execute(nameEncoded, passwordEncrypted);
    }

    /**
     * 转到注册界面
     */
    public void toRegisterOnClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        // finish();
    }

    /**
     * 尝试读取记住的账户，如果有已记住的则填充至输入框中
     */
    public void getRememberedAccount(){
        boolean isRemembered = pref.getBoolean("remember_account", false);

        if (isRemembered) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");

            userNameText.setText(AESUtil.decrypt(szImei,account));
            passwordText.setText(AESUtil.decrypt(szImei,password));

            // "续订"
            rememberAccountCheckBox.setChecked(true);
        }
    }

    /**
     * 尝试记住账户，只有勾选了选择框才会记住
     */
    public void rememberAccount() {
        final SharedPreferences.Editor editor = pref.edit();

        if (rememberAccountCheckBox.isChecked()) {
            editor.putBoolean("remember_account", true);
            editor.putString("account", AESUtil.encrypt(szImei,userNameText.getText().toString()));
            editor.putString("password",AESUtil.encrypt(szImei,passwordText.getText().toString()));
        } else {
            editor.clear();
        }
        editor.apply();
    }
}
