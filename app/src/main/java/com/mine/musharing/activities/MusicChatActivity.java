package com.mine.musharing.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.audio.HotLineRecorder;
import com.mine.musharing.audio.MusicListHolder;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.models.Msg;
import com.mine.musharing.models.Playlist;
import com.mine.musharing.models.User;
import com.mine.musharing.fragments.ChatFragment;
import com.mine.musharing.fragments.MusicFragment;
import com.mine.musharing.fragments.PlaylistFragment;
import com.mine.musharing.fragments.RoomFragment;
import com.mine.musharing.requestTasks.LeaveTask;
import com.mine.musharing.requestTasks.ReceiveTask;
import com.mine.musharing.requestTasks.RequestTaskListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Math.abs;

/**
 * <h1>音乐聊天活动</h1>
 * 播放音乐并有聊天功能。
 *
 * <p>一进去后显示的是控制、显示音乐播放的 MusicFragment</p>
 * <p>上划出现查看、发送消息的 ChatFragment</p>
 * <p>从左边可以划出导航</p>
 * <p>从右边可以划出显示房间成员、播放列表的 RoomFragment + PlaylistFragment</p>
 *
 * <p>消息的刷新接收在次活动中完成</p>
 *
 * <em>Musharing理念的主要实现活动</em>
 */
public class MusicChatActivity extends AppCompatActivity {

    private final int[] TopNavItems = {R.id.top_nav_music, R.id.top_nav_room, R.id.top_nav_playlist, R.id.top_nav_me};

    private int currentNavItem = R.id.top_nav_music;

    private User user;

    private Playlist playlist;

    private DrawerLayout mDrawerLayout;

    private CircleImageView navUserImgView;

    private TextView navUserNameView;

    private MusicFragment musicFragment;

    private ChatFragment chatFragment;

    private RoomFragment roomFragment;

    private PlaylistFragment playlistFragment;

    public RelativeLayout touchShield;

    // Timer Tasks
    private Timer mTimer = new Timer();

    // Msg
    private TimerTask refreshMsgTimerTask;
    private static final long MSG_REFRESH_PERIOD = 2000;

    // Member & Musiclist
    private TimerTask checkMemberMusiclistTimerTask;
    private static final long MEMBER_MUSICLIST_CHECK_PERIOD = 2000;

    private Snackbar memberMusicCheckSnackbar;

    // Drawers
    public static final int DRAWER_NAV = 0;
    public static final int RRAWER_ROOM = 1;

    // reloadFlag
    private boolean reloadFlag = false;

    @SuppressLint("ClickableViewAccessibility")
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
        // playlist = (Playlist) intent.getBundleExtra("data").get("playlist");

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
        bundle.putSerializable("playlist", MusicListHolder.getInstance().getPlaylist());
        bundle.putSerializable("musiclist", MusicListHolder.getInstance().getMusicList());

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
        transaction.add(R.id.main_fragment, musicFragment);

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
                    reloadFlag = false;
                    startActivity(intent1);
                    break;
                case R.id.nav_lookaround:
                    mDrawerLayout.closeDrawers();
                    Intent intent2 = new Intent(MusicChatActivity.this, LookaroundActivity.class);
                    reloadFlag = false;
                    startActivity(intent2);
                    break;
                case R.id.nav_exit:
                    mDrawerLayout.closeDrawers();
                    leaveRoom();
                    break;
                default:
                    Toast.makeText(MusicChatActivity.this, "未完成的功能", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // user's name and img in NavigationView
        navUserImgView = navigationView.getHeaderView(0).findViewById(R.id.nav_user_img_view);
        Glide.with(this).load(user.getImgUrl()).into(navUserImgView);

        navUserNameView = navigationView.getHeaderView(0).findViewById(R.id.nav_user_name_view);
        navUserNameView.setText(user.getName());

        touchShield = findViewById(R.id.touch_shield);
        touchShield.setOnClickListener(null);
        touchShield.setOnTouchListener(null);
        touchShield.setVisibility(View.VISIBLE);

        // 当需要到 RoomPlaylistActivity 选择好友与播放列表时提示用户操作的 Snackbar
        memberMusicCheckSnackbar = Snackbar.make(mDrawerLayout, "需要添加好友和播放列表才能开始分享", Snackbar.LENGTH_INDEFINITE)
                .setAction("去添加", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MusicChatActivity.this, RoomPlaylistActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        intent.putExtra("data", bundle);
                        reloadFlag = true;
                        startActivity(intent);
                    }
                });

        restartTimerTasks();
    }

    /**
     * 总的消息获取地
     *
     * <p>所有地消息都在这里接收，并被分发</p>
     *
     * <em>⚠整个App中必须实现且只实现一次这个方法！⚠</em>
     */
    private void refreshMsgs() {

        // 禁用 MusicListHolder 中的消息接收器
        if (MusicListHolder.getInstance().receiveFlag) {
            MusicListHolder.getInstance().receiveFlag = false;
        }

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
                            case Msg.TYPE_PLAYLIST:
                                boolean changed = MusicListHolder.getInstance().handlePlaylistMsg(msg);
                                if (changed) {
                                    // TODO: Refresh the player!
                                }
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

    private void checkMemberMusiclist() {

        if (roomFragment != null && playlistFragment != null) {
            if (
                    roomFragment.getmMemberList().size() <= 1 ||    // 没加好友
                    MusicListHolder.getInstance().getMusicList().size() < 1   // 没加歌单
            ) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!memberMusicCheckSnackbar.isShown()) {
                            memberMusicCheckSnackbar.show();
                        }
                        if (touchShield.getVisibility() != View.VISIBLE) {
                            touchShield.setVisibility(View.VISIBLE);
                        }
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (memberMusicCheckSnackbar.isShown()) {
                            memberMusicCheckSnackbar.dismiss();
                        }
                        if (touchShield.getVisibility() != View.GONE) {
                            touchShield.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    /**
     * 点击顶部导航栏
     */
    public void topNavItemOnClick(View clicked) {
        int clickedId = clicked.getId();
        if (clickedId != currentNavItem) {
            // ui change
            navUIChange(clickedId);
            // TODO: mainFragment change
            mainFragmentChange(clickedId);
            currentNavItem = clickedId;
        }

    }

    private void navUIChange(int current) {
        int currentIndex = -1;
        for (int i=0; i < TopNavItems.length; i++) {
            if (TopNavItems[i] == current) {
                currentIndex = i;
            }
        }

        int[] textSizes = {0, 0, 0, 0};
        for (int i=0; i < TopNavItems.length; i++) {
            switch (abs(i - currentIndex)) {
                case 3:
                    textSizes[i] = 12;
                    break;
                case 2:
                    textSizes[i] = 14;
                    break;
                case 1:
                    textSizes[i] = 16;
                    break;
                case 0:
                    textSizes[i] = 22;
                    break;
                default:
                    textSizes[i] = 12;
            }
            TextView tv = findViewById(TopNavItems[i]);
            tv.setTextSize(textSizes[i]);
        }
    }

    private void mainFragmentChange(int current) {

    }


    /**
     * 重置定时任务
     */
    private void restartTimerTasks() {
        cancelTimerTasks();
        startTimerTasks();
    }

    /**
     * 取消定时任务
     */
    private void cancelTimerTasks() {
        // 结束定时任务
        if (refreshMsgTimerTask != null) {
            refreshMsgTimerTask.cancel();
        }
        if (checkMemberMusiclistTimerTask != null) {
            checkMemberMusiclistTimerTask.cancel();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    /**
     * 开始定时任务
     */
    private void startTimerTasks() {
        mTimer = new Timer();
        // Refresh the message list periodically
        refreshMsgTimerTask = new TimerTask() {
            @Override
            public void run() {
                refreshMsgs();
            }
        };
        mTimer.schedule(refreshMsgTimerTask, 0, MSG_REFRESH_PERIOD);

        // 检测是否需要到 RoomPlaylistActivity 选择好友与播放列表
        checkMemberMusiclistTimerTask = new TimerTask() {
            @Override
            public void run() {
                checkMemberMusiclist();
            }
        };
        mTimer.schedule(checkMemberMusiclistTimerTask, 200, MEMBER_MUSICLIST_CHECK_PERIOD);
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

    /**
     * 打开指定的抽屉层
     * @param drawer
     */
    public void openDrawer(int drawer) {
        switch (drawer) {
            case DRAWER_NAV:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case RRAWER_ROOM:
                mDrawerLayout.openDrawer(GravityCompat.END);
                break;
        }
    }

    /**
     * <h1>捕获实体按键的按下事件</h1>
     *
     * <p>捕获音量按键事件传给 mainFragment 处理音量键控制</p>
     * <p>捕获返回键，禁止返回（MusicChatActivity 是主界面，不应该退出）</p>
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 捕获音量按键事件传给 mainFragment 处理音量键控制
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return musicFragment.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onRestart() {
        if (reloadFlag) {
            // 为防止错误，重启 Music、Chat Fragment
            musicFragment.onDestroy();

            // 打包要传递给 fragments 的 user 数据
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            bundle.putSerializable("playlist", MusicListHolder.getInstance().getPlaylist());
            bundle.putSerializable("musiclist", MusicListHolder.getInstance().getMusicList());

            // init MusicFragment
            musicFragment = new MusicFragment();
            musicFragment.setArguments(bundle);

            // init ChatFragment
            chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.remove(chatFragment);
            transaction.remove(musicFragment);

            transaction.add(R.id.chat_fragment, chatFragment);
            transaction.add(R.id.main_fragment, musicFragment);

            transaction.addToBackStack(null);
            transaction.commit();

            restartTimerTasks();
        }
        reloadFlag = false;
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        // 结束定时任务
        cancelTimerTasks();

        // destroy fragments
        chatFragment.onDestroy();
        musicFragment.onDestroy();

        // 结束HotLineRecorder
        HotLineRecorder.getInstance().onDestroy();

        // 退出房间
        leaveRoom();

        super.onDestroy();
    }

    /**
     * 退出当前房间
     */
    private void leaveRoom() {
        new LeaveTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {}

            @Override
            public void onSuccess(String s) {
                runOnUiThread(() -> {
//                    Intent intent = new Intent(MusicChatActivity.this, RoomPlaylistActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("user", user);
//                    intent.putExtra("data", bundle);
//                    startActivity(intent);
//                    finish();

                    cancelTimerTasks();
                    MusicListHolder.getInstance().setPlaylist(new Playlist());
                    reloadFlag = true;
                    onRestart();
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
