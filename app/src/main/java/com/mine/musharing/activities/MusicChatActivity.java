package com.mine.musharing.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.MainActivity;
import com.mine.musharing.R;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.fragments.ChatFragment;
import com.mine.musharing.fragments.MusicFragment;
import com.mine.musharing.fragments.PlaylistFragment;
import com.mine.musharing.fragments.RoomFragment;
import com.mine.musharing.requestTasks.LeaveTask;
import com.mine.musharing.requestTasks.RequestTaskListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * <h1>音乐聊天活动</h1>
 * 播放音乐并有聊天功能。
 * <em>Musharing理念的主要实现活动</em>
 */
public class MusicChatActivity extends AppCompatActivity {

    private User user;

    private Playlist playlist;

    private DrawerLayout mDrawerLayout;

    private CircleImageView navUserImgView;

    private TextView navUserNameView;

    private MusicFragment musicFragment;

    private ChatFragment chatFragment;

    private RoomFragment roomFragment;

    private PlaylistFragment playlistFragment;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_chat);
        tv = (TextView) findViewById(R.id.tv);

        // 获取数据
        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("data").get("user");
        playlist = (Playlist) intent.getBundleExtra("data").get("playlist");

        // Show Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show ic_menu
        mDrawerLayout = findViewById(R.id.draw_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        // 处理 fragments
        // 打包要传递给 fragments 的 user 数据
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putSerializable("playlist", playlist);

        // init MusicFragment
        musicFragment = new MusicFragment();
        musicFragment.setArguments(bundle);

        // init ChatFragment
        chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);

        // init RoomFragment
        roomFragment = new RoomFragment();
        roomFragment.setArguments(bundle);

        // init PlaylistFragment
        playlistFragment = new PlaylistFragment();
        playlistFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.chat_fragment, chatFragment);
        transaction.add(R.id.music_fragment, musicFragment);

        transaction.add(R.id.room_in_music_chat_activity, roomFragment);
        transaction.add(R.id.playlist_in_music_chat_activity, playlistFragment);

        transaction.addToBackStack(null);
        transaction.commit();

        // Enable NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_friends);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_friends:
                    mDrawerLayout.closeDrawers();
                    mDrawerLayout.openDrawer(Gravity.END);
                    break;
                case R.id.nav_settings:
                    mDrawerLayout.closeDrawers();
                    Intent intent1 = new Intent(MusicChatActivity.this, SettingActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.nav_exit:
                    mDrawerLayout.closeDrawers();
                    leaveRoom();
                    break;
            }
            return true;
        });

        // user's name and img in NavigationView
        navUserImgView = navigationView.getHeaderView(0).findViewById(R.id.nav_user_img_view);
        Glide.with(this).load(user.getImgUrl()).into(navUserImgView);

        navUserNameView = navigationView.getHeaderView(0).findViewById(R.id.nav_user_name_view);
        navUserNameView.setText(user.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        leaveRoom();
        super.onDestroy();
    }

    private void leaveRoom() {
        new LeaveTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {}

            @Override
            public void onSuccess(String s) {
                runOnUiThread(() -> {
                    // 返回到 RoomPlaylistActivity
                    Intent intent = new Intent(MusicChatActivity.this, RoomPlaylistActivity.class);
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
                    Toast.makeText(MusicChatActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFinish(String s) {}

        }).execute(user.getUid());
    }
    int count = -1;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();

        if (action ==KeyEvent.ACTION_DOWN) {
            tv.setText("+++++++++ACTION_DOWN++++++"+ count++);
            return true;
        }

        if (action== KeyEvent.ACTION_UP) {
            tv.setText("+++++ACTION_UP++++++++++");
            return true;
        }

        return super.dispatchKeyEvent(event);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:

                tv.setText("-----------------"+count);
                count--;

                return true;

            case KeyEvent.KEYCODE_VOLUME_UP:
                tv.setText("++++++++++++++++"+ count);
                count++;
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                tv.setText("MUTE");

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

