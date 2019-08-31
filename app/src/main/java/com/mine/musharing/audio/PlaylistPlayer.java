package com.mine.musharing.audio;

import android.media.MediaPlayer;
import android.util.Log;

import com.mine.musharing.bases.Music;
import com.mine.musharing.bases.Playlist;

import java.io.IOException;

import static android.support.constraint.Constraints.TAG;

/**
 * <h1>列表播放器</h1>
 *
 * Room同步列表循环🔄播放列表中的音乐
 */
public class PlaylistPlayer {

    /**
     * 代表当前播放位置的类
     */
    public class Current {
        private int index;
        private Music music;
        private boolean prepared;

        public Current(int index) {
            this.index = index;
            this.music = playlist.getMusicList().get(index);
            this.prepared = false;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public Music getMusic() {
            return music;
        }

        public void setMusic(Music music) {
            this.music = music;
        }

        public boolean isPrepared() {
            return prepared;
        }

        public void setPrepared(boolean prepared) {
            this.prepared = prepared;
        }
    }

    /**
     * 要播放的播放列表
     */
    private Playlist playlist;

    /**
     * 完成音频播放任务的MediaPlayer
     */
    private MediaPlayer mediaPlayer;

    /**
     * 代表当前播放位置的对象
     */
    private Current current;

    /**
     * 表示有无播放结束信息正在处理，防止重复处理下一曲，导致播放状态在两首歌开头处不停切换，卡死
     */
    private boolean musicOverHandling = false;

    public PlaylistPlayer(Playlist playlist) {
        this.playlist = playlist;
        this.mediaPlayer = new MediaPlayer();
        this.current = new Current(0);

        mediaPlayer.setLooping(false);
    }

    /**
     * 准备播放 current 处的 Music
     */
    private void prepare() {
        Log.d(TAG, "prepare: current: " + current);
        try {
            mediaPlayer.setDataSource(current.music.getFileUrl());
            mediaPlayer.prepare();  // 同步的方式装载流媒体文件
            mediaPlayer.setOnCompletionListener(mp -> musicOver());     // 监听播放完成事件
            current.prepared = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前播放进度
     */
    public int getCurrentProgress() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getMaxProgress() {
        return mediaPlayer.getDuration();
    }
    /**
     * 开始从同步处播放
     */
    public void start() {
        start(PlayAsyncer.getInstance().getAsyncPlayIndex(), PlayAsyncer.getInstance().getAsyncPlayProgress(), 1);
    }

    /**
     * 开始播放指定位置、指定进度的曲目，也可以用来做player.seekTo用
     *
     * @param index 曲目在列表中的位置索引
     * @param seekToProgress 播放进度
     * @param prepareDelaySkip 在播放开始时跳过准备过程中的延迟权重 (用 `0` 表示不计入，`1`表示要跳过)
     */
    public void start(int index, int seekToProgress, int prepareDelaySkip) {
        long timeBeforePrepare = System.currentTimeMillis();
        if (current.index != index) {
            nextTo(index);
        }
        if (!current.prepared) {
            prepare();
        }

        long timeAfterPrepare = System.currentTimeMillis();
        long delay = prepareDelaySkip * (timeAfterPrepare - timeBeforePrepare);
        Log.d(TAG, "start: seekToProgress + (int)delay = " + seekToProgress + (int)delay);

        mediaPlayer.seekTo(seekToProgress + (int)delay);
        if (!isPlaying()) {
            mediaPlayer.start();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (isPlaying()) {
            mediaPlayer.pause();
            // playAsyncer.postPaused(current.index, getCurrentProgress());
        }
    }

//    /**
//     * 同步，处理来自其他人的start/pause/next
//     */
//    public void async() throws PlayAsyncer.FailedToAsync {
//        int state = playAsyncer.getState(current.index);
//        Log.d(TAG, "async: state: " + state);
//        switch (state) {
//            case PlayAsyncer.SHOULD_CONTINUE:
//                // 不用处理，继续播放
//                break;
//            case PlayAsyncer.SHOULD_START:
//                start();
//                break;
//            case PlayAsyncer.SHOULD_PAUSE:
//                pause();
//                break;
//            case PlayAsyncer.SHOULD_NEXT:
//                next();
//                break;
//            default:
//                throw new PlayAsyncer.FailedToAsync();
//        }
//        playAsyncer.notifyAsynced();
//    }

    /**
     * 一首歌结束，准备并开始播放下一首
     */
    public void musicOver() {
        Log.d(TAG, "musicOver: handling = " + musicOverHandling);
        if (!musicOverHandling) {
            musicOverHandling = true;
            playNext();
            PlayAsyncer.getInstance().postNext();
            musicOverHandling = false;
        }
    }
    /**
     * 开始播放下一曲
     *
     * <em>(列表循环)</em>
     */
    public void playNext() {
        int index = current.index + 1;
        if (index >= playlist.getSize()) {   // 没有了，从头来过
            index = 0;
        }
        nextTo(index);
        start(current.getIndex(), 0, 0);
    }

    /**
     * 将current定位到播放列表中的某一首
     */
    public void nextTo(int index) {
        if (current.index != index && index < playlist.getSize()) {
            mediaPlayer.reset();
            current.index = index;
            current = new Current(current.index);
        }
    }

    /**
     * Checks whether the PlaylistPlayer is playing.
     *
     * @return true if currently playing, false otherwise
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 获取当前的 Current 对象
     * @return 当前的 Current 对象
     */
    public Current getCurrent() {
        return current;
    }
}
