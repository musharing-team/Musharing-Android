package com.mine.musharing.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mine.musharing.MainActivity;
import com.mine.musharing.R;
import com.mine.musharing.audio.MusicListHolder;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.fragments.PlaylistFragment;
import com.mine.musharing.fragments.RoomFragment;
import com.mine.musharing.requestTasks.PlaylistTask;
import com.mine.musharing.requestTasks.RequestTaskListener;

/**
 * <h1>房间/播放列表活动</h1>
 * 选择添加同伴及播放列表的活动
 */
public class RoomPlaylistActivity extends AppCompatActivity {

    private static final String TAG = "RoomPlaylistActivity";

    private User user;

    private RoomFragment roomFragment;

    private PlaylistFragment playlistFragment;

    private ImageButton intoRoomButton;

    private ProgressBar intoRoomProgressBar;

    private CardView playlistCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_room_playlist);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        intoRoomButton = findViewById(R.id.into_room_arrow);
        intoRoomButton.setVisibility(View.VISIBLE);

        intoRoomProgressBar = findViewById(R.id.into_room_progressbar);
        intoRoomProgressBar.setVisibility(View.GONE);

        Intent intent = getIntent();
        user = (User) intent.getBundleExtra("data").get("user");
        Log.d(TAG, "onCreate: user: " + user);

        // playlistCardView
        playlistCardView = findViewById(R.id.playlist_card);
        playlistCardView.setOnClickListener(this::playlistCardOnClick);

        initFragments();
    }

//    private void initMusicList(String idOfCategory) {
//
//        new PlaylistTask(new RequestTaskListener<Playlist>() {
//            @Override
//            public void onStart() {
//                intoRoomButton.setVisibility(View.GONE);
//                intoRoomProgressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onSuccess(Playlist s) {
//                runOnUiThread(() -> {
//                    initFragments(s);
//                });
//            }
//
//            @Override
//            public void onFailed(String error) {
//                runOnUiThread(() -> {
//                    Toast.makeText(RoomPlaylistActivity.this, error, Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onFinish(String s) {
//                intoRoomButton.setVisibility(View.VISIBLE);
//                intoRoomProgressBar.setVisibility(View.GONE);
//            }
//        }).execute(user.getUid(), idOfCategory);
//
//        // musicListHolder.musicList = Demo.testMusicList;
//
//    }

    private void initFragments() {
        // 打包要传递给 fragments 的 user 数据
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putSerializable("playlist", MusicListHolder.getInstance().getPlaylist());
        bundle.putSerializable("musiclist", MusicListHolder.getInstance().getMusicList());

        // roomFragment
        roomFragment = new RoomFragment();
        roomFragment.setArguments(bundle);

        // playlistFragment
        playlistFragment = new PlaylistFragment();
        playlistFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.attend_room_layout, roomFragment);
        transaction.add(R.id.check_playlist_layout, playlistFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void playlistCardOnClick(View view) {
        Intent intent = new Intent(this, CategoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    public void intoChatRoomOnClick(View view) {
        intoRoomButton.setVisibility(View.GONE);
        intoRoomProgressBar.setVisibility(View.VISIBLE);

        if (roomFragment.getmMemberList().size() > 1) {     // 已经加入房间
            Intent intent = new Intent(this, MusicChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            // bundle.putSerializable("playlist", playlistFragment.getPlaylist());
            intent.putExtra("data", bundle);
            startActivity(intent);
            finish();
        } else {    // 没加入房间
            Snackbar.make(findViewById(R.id.attend_room_layout),
                    "至少添加一位朋友才能开始哦^_^", Snackbar.LENGTH_LONG).show();
        }

        intoRoomButton.setVisibility(View.VISIBLE);
        intoRoomProgressBar.setVisibility(View.GONE);
    }
}
