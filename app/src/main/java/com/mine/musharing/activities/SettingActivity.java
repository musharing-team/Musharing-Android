package com.mine.musharing.activities;

import android.content.Intent;
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
import com.mine.musharing.utils.Utility;

/**
 * 设置界面
 */
public class SettingActivity extends AppCompatActivity {
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

    public void nonImplementSettingOnClick(View view) {
        Snackbar.make(findViewById(R.id.setting_layout), "暂不可用", Snackbar.LENGTH_LONG).show();
    }

    public void aboutSettingOnClick(View view) {
        runOnUiThread(() -> {
            Intent intent = new Intent(this, AboutActivity.class);
            Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingActivity.this).toBundle();
            startActivity(intent, translateBundle);
        });
    }
}