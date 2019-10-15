package com.mine.musharing.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mine.musharing.R;

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
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}