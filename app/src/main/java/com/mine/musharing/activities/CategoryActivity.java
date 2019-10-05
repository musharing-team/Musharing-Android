package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mine.musharing.R;
import com.mine.musharing.bases.User;
import com.mine.musharing.fragments.CategoryFragment;

public class CategoryActivity extends AppCompatActivity {

    private static final String TAG = "CategotyActivity";
    private User user;

    private CategoryFragment categoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("data").get("user");
        Log.d(TAG, "onCreate: user: " + user);

        // Show Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("播放列表");
        }

        // 打包要传递给 fragments 的 user 数据
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        // categoryFragment
        categoryFragment = new CategoryFragment();
        categoryFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.category_fragment_frame, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
}
