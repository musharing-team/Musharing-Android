package com.mine.musharing.activities;

import android.Manifest;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String szImei;
    private EditText userNameText;
    private EditText passwordText;
    private CheckBox rememberPass;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        final CheckBox rememberPass = findViewById(R.id.remember_account);
        final Button loginButton = findViewById(R.id.login_button);
        final EditText userNameText = findViewById(R.id.login_user_name);
        final EditText passwordText = findViewById(R.id.login_password);

        //引入imei加密后产生密匙
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String szImei = deviceUuid.toString();

        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        getRememberedAccount();
    }

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
    public void getRememberedAccount(){
        // 读取记住的账户
        boolean isRemember = pref.getBoolean("remember_account", false);
        if (isRemember) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");

            userNameText.setText(AESUtil.decrypt(szImei,account));
            passwordText.setText(AESUtil.decrypt(szImei,password));

            rememberPass.setChecked(true);
            loginButton.setEnabled(true);
        }
    }
    public void rememberAccount() {
        final SharedPreferences.Editor editor = pref.edit();

        if (rememberPass.isChecked()) {
            editor.putBoolean("remember_account", true);
            editor.putString("account", AESUtil.encrypt(szImei,userNameText.getText().toString()));
            editor.putString("password",AESUtil.encrypt(szImei,passwordText.getText().toString()));

        } else {
            editor.clear();
        }
        editor.apply();
    }
}
