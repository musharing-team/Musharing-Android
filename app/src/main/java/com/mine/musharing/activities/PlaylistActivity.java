package com.mine.musharing.activities;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.bases.Category;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.fragments.PlaylistFragment;

import java.util.List;

/**
 * 显示播放列表详情并选择的活动
 */
public class PlaylistActivity extends AppCompatActivity {

    private User user;

    private Category category;

    private Playlist playlist;

    private List<Music> musicList;

    private PlaylistFragment playlistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent intent =  getIntent();

        Bundle bundle = intent.getBundleExtra("data");

        user = (User) intent.getBundleExtra("data").get("user");
        category = (Category) intent.getBundleExtra("data").get("category");
        playlist = (Playlist) intent.getBundleExtra("data").get("playlist");
        musicList = (List<Music>) intent.getBundleExtra("data").get("musiclist");

        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        ImageView categoryImageView = findViewById(R.id.category_image_view);
        TextView categoryContentView = findViewById(R.id.category_content_text);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbarLayout.setTitle(category.getTitle());
        Glide.with(this).load(category.getImage()).into(categoryImageView);
        categoryContentView.setText(category.getDescription());

        playlistFragment = new PlaylistFragment();
        playlistFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.playlist_fragment_layout, playlistFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 返回上一页
     */
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
