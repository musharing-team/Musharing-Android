package com.mine.musharing.fragments;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mine.musharing.R;
import com.mine.musharing.activities.LoginActivity;
import com.mine.musharing.audio.HotLineRecorder;
import com.mine.musharing.audio.MusicListHolder;
import com.mine.musharing.audio.PlayAsyncer;
import com.mine.musharing.audio.PlaylistPlayer;
import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.RecordingDialogManager;
import com.mine.musharing.bases.User;
import com.mine.musharing.recyclerViewAdapters.MsgAdapter;
import com.mine.musharing.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.constraint.Constraints.TAG;

/**
 * <h1>音乐播放的碎片</h1>
 */
public class MusicFragment extends Fragment {

    private static final String TAG = "MusicFragment";

    // 该 Fragment 的 view
    private View musicFragmentView;

    // 数据
    private User user;
    // private Playlist playlist;

    // 同步播放
    private PlayAsyncer playAsyncer;
    private PlaylistPlayer playlistPlayer;

    private TextView titleTextView;
    private TextView artistTextView;

    private Button playButton;
    private TextView playStatusTextView;
    private ProgressBar progressBar;
    private SeekBar volumeBar;
    private TextView playedTime;
    private TextView residueTime;
    private ImageButton hotLineButton;

    // 定时任务
    private Timer mTimerForMusic;
    private TimerTask refreshMusicTimerTask;
    private static final int MUSIC_REFRESH_PERIOD = 1000;

    // 音量控制
    private AudioManager audioManager;
    private int currentVolume;
    private int maxVolume;

    private int currentIndex = -1;

    // Hot line
    private HotLineRecorder hotLineRecorder;
    private RecordingDialogManager recordingDialogManager;

    // Msg
    private RecyclerView msgRecyclerView;
    private List<Msg> mMsgList = new ArrayList<>();
    private MsgAdapter adapter;


    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        musicFragmentView = inflater.inflate(R.layout.fragment_music, container, false);

        // get User & Playlist
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            // playlist = (Playlist) getArguments().getSerializable("playlist");
        } else {
            Toast.makeText(getContext(), "系统异常，请重新登录", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        initHotlineRecorder();
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
     * 点击 HotLine 的事件
     */
    public boolean hotLineOnTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "HotLine button -> down");
                // UI 更新
                try {
                    // 一个震动效果
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                } finally {
                    // 对话框效果
                    recordingDialogManager.showRecordingDialog();
                    recordingDialogManager.recording();

                }

                try {
                    hotLineRecorder.reset();
                    hotLineRecorder.startRecord();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    hotLineRecorder.reset();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "HotLine button -> up");
                // UI 更新
                try {
                    // 一个震动效果
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);
                } finally {
                    // 对话框效果
                    recordingDialogManager.dismissDialog();
                }
                try {
                    hotLineRecorder.stopRecord();
                    hotLineRecorder.publishRecord();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    hotLineRecorder.reset();
                }

                break;

        }
        return false;
    }

    /**
     * 初始化HotlineRecorder 以及 录音时管理UI效果的RecordingDialogManager
     */
    private void initHotlineRecorder() {
        hotLineRecorder = HotLineRecorder.getInstance();
        hotLineRecorder.setUser(user);
        hotLineRecorder.reset();
        recordingDialogManager = new RecordingDialogManager(getContext());
    }

    /**
     * 初始化UI组件
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
//        screenLayout = musicFragmentView.findViewById(R.id.screen_layout);
//        // screenLayout.setBackgroundColor(Utility.randomCardColor());
//        screenBackground = (GradientDrawable) screenLayout.getBackground();
//        screenBackground.setColor(Utility.randomCardColor());

        // Title
        titleTextView = musicFragmentView.findViewById(R.id.play_title_text);
        artistTextView = musicFragmentView.findViewById(R.id.play_artist_text);

        // Play/Pause button
        playButton = musicFragmentView.findViewById(R.id.play_button);
        playStatusTextView = musicFragmentView.findViewById(R.id.play_status_text);
        playButton.setOnClickListener(this::playOrPauseOnClick);

        // Progress
        progressBar = musicFragmentView.findViewById(R.id.play_progress_bar);
        playedTime = musicFragmentView.findViewById(R.id.play_time);
        residueTime = musicFragmentView.findViewById(R.id.play_residue);

        // Volume
        volumeBar = musicFragmentView.findViewById(R.id.play_volume);

        // Hot line button
        hotLineButton = musicFragmentView.findViewById(R.id.hotline_in_music_fragment);
        hotLineButton.setOnTouchListener(this::hotLineOnTouch);

        // message recycler view
        msgRecyclerView = musicFragmentView.findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(user, mMsgList);
        msgRecyclerView.setAdapter(adapter);


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
        Log.d(TAG, "initPlayer: MusicList: " + MusicListHolder.getInstance().getMusicList());
        playlistPlayer = new PlaylistPlayer(MusicListHolder.getInstance().getMusicList());
        playAsyncer = PlayAsyncer.getInstance();
        playAsyncer.setUser(user);
        playAsyncer.setPlayer(playlistPlayer);
    }

    /**
     * 初始化定时刷新任务
     */
    private void initTimerTask() {
        mTimerForMusic = new Timer();
        refreshMusicTimerTask = new TimerTask() {
            @Override
            public void run() {
                updateUi();
            }
        };
        mTimerForMusic.schedule(refreshMusicTimerTask, 0, MUSIC_REFRESH_PERIOD);
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

                // screenLayout.setBackgroundColor(Utility.randomCardColor());
                // screenBackground.setColor(Utility.randomCardColor());        这个更改会造成 Activity 意外结束
            }

            // 播放/暂停按钮
            if (playlistPlayer.isPlaying()) {
                playStatusTextView.setText("暂停");
                playButton.setBackgroundResource(R.drawable.button_start);
            } else {
                playStatusTextView.setText("播放");
                playButton.setBackgroundResource(R.drawable.button_stop);
            }

            // 刷新进度条
            setProgress(playlistPlayer.getCurrentProgress(), playlistPlayer.getMaxProgress());
        });
    }

    /**
     * 刷新播放进度条显示，并对应刷新两端显示的时间
     * @param currentProgress current playing progress in milliseconds
     * @param maxProgress max progress
     */
    private void setProgress(int currentProgress, int maxProgress) {
        progressBar.setMax(maxProgress);
        progressBar.setProgress(currentProgress);
        playedTime.setText(Utility.formatMusicProgress(currentProgress));
        residueTime.setText(Utility.formatMusicProgress(currentProgress - maxProgress));
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
     * 实体按键处理<br/>
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

    /**
     * 显示一条消息
     * @param msg
     */
    public void showTextMsg(Msg msg) {
        mMsgList.add(msg);
        adapter.notifyItemInserted(mMsgList.size() - 1); // 有新消息,刷新显示
        msgRecyclerView.scrollToPosition(mMsgList.size() - 1);   // 移动到最后一条消息
    }

    /**
     * 移除过多的消息
     */
    public void clearSurplusMsgs(int retainCount) {
        int removed = 0;
        while (mMsgList.size() > retainCount) {
            mMsgList.remove(0);
            removed++;
        }
        adapter.notifyItemRangeRemoved(0, removed);
    }

    @Override
    public void onDestroy() {
        // 结束定时刷新音乐进度的任务
        if (refreshMusicTimerTask != null) {
            refreshMusicTimerTask.cancel();
        }
        if (mTimerForMusic != null) {
            mTimerForMusic.cancel();
        }

        super.onDestroy();
    }
}
