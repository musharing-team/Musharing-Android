package com.mine.musharing.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.models.User;
import com.mine.musharing.utils.Utility;

/**
 * 设置界面
 */
public class SettingActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Show Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_setting);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("设置");
        }

        setupTransition();

        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("data").get("user");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void nonImplementSettingOnClick(View view) {
//        Snackbar.make(findViewById(R.id.setting_layout), "暂不可用", Snackbar.LENGTH_LONG).show();
//    }
//
//    public void aboutSettingOnClick(View view) {
//        runOnUiThread(() -> {
//            Intent intent = new Intent(this, AboutActivity.class);
//            Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingActivity.this).toBundle();
//            startActivity(intent, translateBundle);
//        });
//    }

    public void settingItemOnClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.setting_about:
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.setting_help_feedback:
                intent = new Intent(this, FeedbackActivity.class);
                break;
            case R.id.setting_notice:
                // Reference: https://stackoverflow.com/questions/32366649/any-way-to-link-to-the-android-notification-settings-for-my-app
                intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getPackageName());
                    intent.putExtra("app_uid", getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                }
                break;
            case R.id.setting_user:
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent = new Intent(this, UserSettingActivity.class);
                intent.putExtra("data", bundle);
            default:
                Snackbar.make(findViewById(R.id.setting_layout), "暂不可用", Snackbar.LENGTH_LONG).show();
        }

        if (intent != null) {
            Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingActivity.this).toBundle();
            startActivity(intent, translateBundle);
        }
    }
}