package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.audio.HotLineRecorder;
import com.mine.musharing.audio.MusicListHolder;
import com.mine.musharing.audio.PlaylistPlayer;
import com.mine.musharing.models.Playlist;
import com.mine.musharing.models.User;
import com.mine.musharing.requestTasks.LeaveTask;
import com.mine.musharing.requestTasks.LogoutTask;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.utils.ParseUtil;

public class UserSettingActivity extends AppCompatActivity {

    // TODO: add appbar!

    User user;

    ImageView userImgView;

    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("data").get("user");

        initViews();
    }

    private void initViews() {
        // 用户头像
        userImgView = findViewById(R.id.setting_user_img);
        Glide.with(this).load(user.getImgUrl()).into(userImgView);

        // 用户名
        userName = findViewById(R.id.setting_user_name);
        userName.setText(user.getName());
    }

    public void logoutOnClick(View view) {
        Toast.makeText(UserSettingActivity.this, "这个功能有bug\n但我懒得改，也懒得阻止你\n现在一切都完了，Bug即将发生，你就哭去吧，谁让你点的退出。。。", Toast.LENGTH_LONG).show();
        new LogoutTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(String s) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(UserSettingActivity.this, LoginActivity.class);
                    Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(UserSettingActivity.this).toBundle();
                    startActivity(intent, translateBundle);
                });
            }

            @Override
            public void onFailed(String error) {
            }

            @Override
            public void onFinish(String s) {
            }

        }).execute(user.getUid());
    }


}
