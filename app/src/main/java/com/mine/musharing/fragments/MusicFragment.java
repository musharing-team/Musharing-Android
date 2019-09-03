package com.mine.musharing.fragments;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.audio.PlaylistPlayer;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.utils.Utility;

import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

/**
 * <h1>音乐播放的碎片</h1>
 */
public class MusicFragment extends Fragment {

    // 该 Fragment 的 view
    private View musicFragmentView;

    // 数据
    private User user;
    private Playlist playlist;

    // 同步播放
    private PlayAsyncer playAsyncer;
    private PlaylistPlayer playlistPlayer;

    // views
    private TextView titleTextView;
    private TextView artistTextView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private SeekBar volumeBar;
    private TextView playedTime;
    private TextView residueTime;

    // 定时任务
    private Timer mTimer;
    private TimerTask mTimerTask;
    private static final int REFRESH_PERIOD = 1000;

    // 音量控制
    private AudioManager audioManager;
    private int currentVolume;
    private int maxVolume;

    private int currentIndex = -1;

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        ⚠️[注意]: 👇 保持下面这种顺序！👇
        First of all, inflate the layout
        Then, get User & Playlist
        After that, call init*() according to priority
         */
        // Inflate the layout for this fragment
        musicFragmentView = inflater.inflate(R.layout.fragment_music, container, false);

        // get User & Playlist
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            playlist = (Playlist) getArguments().getSerializable("playlist");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        // inits
        initViews();
        initVolumeControl();
        initPlayer();
        initTimerTask();

        return musicFragmentView;
    }

    /**
     * 处理播放/暂停一体的按钮的点击事件
     * @param view
     */
    public void playOrPauseOnClick(View view) {
        Log.d(TAG, "playOrPauseOnClick: playlistPlayer.isPlaying: " + playlistPlayer.isPlaying());
        if (playlistPlayer.isPlaying()) {
            playlistPlayer.pause();
            playAsyncer.postPaused();
        } else {
            playlistPlayer.start();
            playAsyncer.postStarted();
        }
    }

    /**
     * 初始化UI组件
     */
    private void initViews() {
        titleTextView = musicFragmentView.findViewById(R.id.play_title_text);
        artistTextView = musicFragmentView.findViewById(R.id.play_artist_text);
        imageView = musicFragmentView.findViewById(R.id.play_image_view);
        progressBar = musicFragmentView.findViewById(R.id.play_progress_bar);
        volumeBar = musicFragmentView.findViewById(R.id.play_volume);
        playedTime = musicFragmentView.findViewById(R.id.play_time);
        residueTime = musicFragmentView.findViewById(R.id.play_residue);

        // 把图片作为处理播放/暂停一体的按钮
        imageView.setOnClickListener(this::playOrPauseOnClick);
    }

    /**
     * 初始化音量控制
     */
    private void initVolumeControl() {
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        volumeBar.setOnSeekBarChangeListener(new VolumeBarChangeListener());

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setMax(maxVolume);

        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeBar.setProgress(currentVolume);
    }

    /**
     * 初始化PlayAsyncer & PlaylistPlayer
     */
    private void initPlayer() {
        playlistPlayer = new PlaylistPlayer(playlist);
        playAsyncer = PlayAsyncer.getInstance();
        playAsyncer.setUser(user);
        playAsyncer.setPlayer(playlistPlayer);
    }

    /**
     * 初始化定时刷新任务
     */
    private void initTimerTask() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                updateUi();
            }
        };
        mTimer.schedule(mTimerTask, 0, REFRESH_PERIOD);
    }

    /**
     * 跟新显示的曲目信息、播放暂停、播放进度
     */
    private void updateUi() {
        getActivity().runOnUiThread(() -> {
            // 曲目信息更新
            if (playlistPlayer.getCurrent().getIndex() != currentIndex) {
                currentIndex = playlistPlayer.getCurrent().getIndex();

                Music currentMusic = playlistPlayer.getCurrent().getMusic();

                titleTextView.setText(currentMusic.getName());
                artistTextView.setText(currentMusic.getArtist());
                progressBar.setMax(playlistPlayer.getMaxProgress());
            }

            // 作为播放/暂停按钮的图片
            if (playlistPlayer.isPlaying()) {
                Glide.with(musicFragmentView).load(R.drawable.ic_pause_blue).into(imageView);
            } else {
                Glide.with(musicFragmentView).load(R.drawable.ic_play_blue).into(imageView);
            }

            // 刷新进度条
            setProgress(playlistPlayer.getCurrentProgress());
        });
    }

    /**
     * 刷新播放进度条显示，并对应刷新两端显示的时间
     * @param progress playing progress in milliseconds
     */
    private void setProgress(int progress) {
        progressBar.setProgress(progress);
        playedTime.setText(Utility.formatMusicProgress(progress));
        residueTime.setText(Utility.formatMusicProgress(progress - progressBar.getMax()));
    }

    /**
     * 音量处理
     */
    class VolumeBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        //当拖动条的滑块位置发生改变时触发该方法
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            currentVolume = progress;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        }
    }

    /**
     * 按键处理<br/>
     *
     * 用来处理音量键事件
     * 由于在 fragment 中不能直接 onKeyDown，所以要在 activity 里 onKeyDown，然后把事件通过这个传进来
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                currentVolume += 1;
                if (currentVolume > maxVolume) {
                    currentVolume = maxVolume;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                currentVolume -= 1;
                if (currentVolume < 0) {
                    currentVolume = 0;
                }
                break;
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        volumeBar.setProgress(currentVolume);

        return true;
    }
}
