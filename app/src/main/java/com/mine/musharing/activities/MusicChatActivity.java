package com.mine.musharing.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.MainActivity;
import com.mine.musharing.R;
import com.mine.musharing.audio.HotLineRecorder;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.fragments.ChatFragment;
import com.mine.musharing.fragments.MusicFragment;
import com.mine.musharing.fragments.PlaylistFragment;
import com.mine.musharing.fragments.RoomFragment;
import com.mine.musharing.recyclerViewAdapters.MsgAdapter;
import com.mine.musharing.requestTasks.LeaveTask;
import com.mine.musharing.requestTasks.ReceiveTask;
import com.mine.musharing.requestTasks.RequestTaskListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

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

    // Msg
    private Timer mTimerForMsg;

    private TimerTask refreshMsgTimerTask;

    private static final long MSG_REFRESH_PERIOD = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_chat);

        // permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        // 获取数据
        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("data").get("user");
        playlist = (Playlist) intent.getBundleExtra("data").get("playlist");

        // Show ic_menu
        mDrawerLayout = findViewById(R.id.draw_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            // actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
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

        // Refresh the message list periodically
        mTimerForMsg = new Timer();
        refreshMsgTimerTask = new TimerTask() {
            @Override
            public void run() {
                refreshMsgs();
            }
        };
        mTimerForMsg.schedule(refreshMsgTimerTask, 0, MSG_REFRESH_PERIOD);
    }

    /**
     * 总的消息获取地
     *
     * <p>所有地消息都在这里接收，并被分发</p>
     *
     * <em>⚠整个App中必须实现且只实现一次这个方法！⚠</em>
     */
    private void refreshMsgs() {

        new ReceiveTask(new RequestTaskListener<List<Msg>>() {
            @Override
            public void onStart() {}

            @Override
            public void onSuccess(List<Msg> newMsgs) {
                if (!newMsgs.isEmpty()) {
                    Log.d(TAG, "refreshMsgs: new Msgs: " + newMsgs);
                }
                runOnUiThread(() -> {
                    for (Msg msg : newMsgs) {
                        switch (msg.getType()) {
                            case Msg.TYPE_TEXT:
                                musicFragment.showTextMsg(msg);
                                chatFragment.showTextMsg(msg);
                                break;
                            case Msg.TYPE_PLAYER_ASYNC:
                                PlayAsyncer.getInstance().handleAsyncMsg(msg);
                                break;
                            case Msg.TYPE_RECORD:
                                HotLineRecorder.getInstance().handleRecordMsg(msg);
                                Msg recordSignMsg = new Msg(msg.TYPE_TEXT, new User(msg.getFromUid(), msg.getFromName(), msg.getFromImg()), "[语音]");
                                musicFragment.showTextMsg(recordSignMsg);
                                chatFragment.showTextMsg(recordSignMsg);
                        }
                    }
                    // 清除过多的历史消息
                    musicFragment.clearSurplusMsgs(1);
                    chatFragment.clearSurplusMsgs(20);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 捕获音量按键事件传给 musicFragment 处理音量键控制
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return musicFragment.onKeyDown(keyCode, event);
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        // 结束定时刷新消息的任务
        if (refreshMsgTimerTask != null) {
            refreshMsgTimerTask.cancel();
        }
        if (mTimerForMsg != null) {
            mTimerForMsg.cancel();
        }
        // destroy fragments
        chatFragment.onDestroy();
        musicFragment.onDestroy();
        // 退出房间
        leaveRoom();
        // 结束HotLineRecorder
        HotLineRecorder.getInstance().onDestroy();

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


}
