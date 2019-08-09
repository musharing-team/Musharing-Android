package com.mine.musharing.asyncPlayer;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mine.musharing.bases.Msg;
import com.mine.musharing.bases.Playlist;
import com.mine.musharing.bases.User;
import com.mine.musharing.requestTasks.RequestTaskListener;
import com.mine.musharing.requestTasks.SendTask;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Dictionary;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * <h1>播放器同步处理</h1>
 *
 * 用来处理同一个Room中各个播放器的同步
 */
public class PlayAsyncer {

    /**
     * <h2>同步信息类</h2>
     *
     * 一个可以toString成作为Msg的Content的对象的类，包含同步需要的信息
     */
    class AsyncContent {
        private String type;
        private User user;
        private int index;
        private int progress;
        private long time;

        public AsyncContent(String type, User user, int index, int progress, long time) {
            this.type = type;
            this.user = user;
            this.index = index;
            this.progress = progress;
            this.time = time;
        }

        /**
         * toString 把AsyncContent转化成json
         *
         * @return 该AsyncContent转化成的json
         */
        @Override
        public String toString() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

    /**
     * AsyncContent的type，暂时弃用
     */
    public static final String TYPE_REQUEST_START = "request_start";

    /**
     * AsyncContent的type，代表开始播放
     */
    public static final String TYPE_STARTED = "started";

    /**
     * AsyncContent的type，代表暂停播放
     */
    public static final String TYPE_PAUSED = "paused";

    /**
     * AsyncContent的type，代表播放下一首
     */
    public static final String TYPE_NEXT = "next";

    /**
     * 暂存正在发送的Msg，用来防止用户恶意快速操作下类似的Msg不停发送消耗资源并导致同步系统混乱
     */
    public Msg sendingAsyncMsg;

    /**
     * 当前登录的用户
     */
    private User user;

    /**
     * 绑定的本地播放器
     */
    private PlaylistPlayer playlistPlayer;

    /**
     * 接收到的同步消息列表
     */
    private List<AsyncContent> asyncContentList = new ArrayList<>();

    /**
     * 当无法同步时抛出
     */
    public static class FailedToAsync extends RuntimeException {
        public FailedToAsync(String message) {
            super(message);
        }
    }

    public PlayAsyncer() {}

    /**
     * 单例Holder
     */
    private static class SingletonHolder {
        private static PlayAsyncer instance = new PlayAsyncer();
    }

    /**
     * 获取PlayAsyncer的单例
     * @return PlayAsyncer的单例
     */
    public static PlayAsyncer getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 设置当前用户
     *
     * <em>必须在适当位置设置这个，然后PlayAsyncer才能正常工作</em>
     * @param user 当前用户
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 设置播放器
     *
     * <em>必须在适当位置设置这个，然后PlayAsyncer才能正常工作</em>
     * @param player 播放器
     */
    public void setPlayer(PlaylistPlayer player) {
        playlistPlayer = player;
    }

    /**
     * 在收到同步消息时处理，完成同步，即完成来自他人的播放/暂停/下一曲
     * @param msg 收到的同步消息
     */
    public void handleAsyncMsg(Msg msg) {
        Gson gson = new Gson();
        AsyncContent asyncContent = gson.fromJson(msg.getContent(), AsyncContent.class);
        asyncContentList.add(asyncContent);
        if (asyncContent.user.equals(this.user)) {      // 自己发出的不要管
            return;
        }
        Log.d(TAG, "handleAsyncMsg: from_user" + asyncContent.user + ", type: " + asyncContent.type);
        switch (asyncContent.type) {
            case TYPE_REQUEST_START:
                if (playlistPlayer.isPlaying()) {
                    postStarted(playlistPlayer.getCurrent().getIndex(),
                            playlistPlayer.getCurrentProgress());
                }
            case TYPE_STARTED:
                playlistPlayer.start(asyncContent.index,
                        asyncContent.progress +
                        (int)(System.currentTimeMillis() - asyncContent.time), 1);
                break;
            case TYPE_PAUSED:
                playlistPlayer.pause();
                break;
            case TYPE_NEXT:
                if (asyncContent.index > playlistPlayer.getCurrent().getIndex() &&
                playlistPlayer.getMaxProgress() - playlistPlayer.getCurrentProgress() > 1000) {
                    playlistPlayer.playNext();
                }
                break;
            default:
                throw new FailedToAsync("Unknown async msg type (asyncContent.type): " + asyncContent.type);
        }
        updateAsyncContentList(asyncContent.index);
    }

    /**
     * 告知全Room，从当前播放位置开始播放开始播放
     */
    public void postStarted() {
        int index = playlistPlayer.getCurrent().getIndex();
        int progress = playlistPlayer.getCurrentProgress();
        postStarted(index, progress);
    }

    /**
     * 告知全Room，从当前播放位置暂停播放
     */
    public void postPaused() {
        int index = playlistPlayer.getCurrent().getIndex();
        int progress = playlistPlayer.getCurrentProgress();
        postPaused(index, progress);
    }

    /**
     * 告知全Room，从当前播放位置播放下一首
     */
    public void postNext() {
        int index = playlistPlayer.getCurrent().getIndex();
        int progress = playlistPlayer.getCurrentProgress();
        postNext(index, progress);
    }


    /**
     * 告知全Room，从某一播放位置开始播放
     *
     * @param index 要开始播放的曲目在Playlist中的索引
     * @param progress 指定曲目要开始播放的位置(seekTo, milliseconds)
     */
    public void postStarted(int index, int progress) {
        sendAsyncMsg(TYPE_STARTED, index, progress);
    }

    /**
     * 告知全Room，从某一播放位置暂停播放
     *
     * @param index 要暂停播放的曲目在Playlist中的索引
     * @param progress 指定曲目要暂停播放的位置(seekTo, milliseconds)
     */
    public void postPaused(int index, int progress) {
        sendAsyncMsg(TYPE_PAUSED, index, progress);
    }

    /**
     * 告知全Room，播放位于某一播放位置的下一曲
     *
     * @param index 要播放的曲目在Playlist中的索引
     * @param progress 指定曲目要开始播放的位置(seekTo, milliseconds)
     */
    public void postNext(int index, int progress) {
        sendAsyncMsg(TYPE_NEXT, index, progress);
    }

    /**
     * 获取同步Room中当前播放曲目索引
     *
     * @return 同步的曲目索引
     */
    public int getAsyncPlayIndex() {
        int index = 0;
        if (!asyncContentList.isEmpty()) {
            AsyncContent last = asyncContentList.get(asyncContentList.size() - 1);
            index = last.index;
        }
        return index;
    }

    /**
     * 获取同步Room中当前播放曲目播放进度
     *
     * @return 同步的播放进度(milliseconds)
     */
    public int getAsyncPlayProgress() {
        int progress = 0;
        if (!asyncContentList.isEmpty()) {
            AsyncContent last = asyncContentList.get(asyncContentList.size() - 1);

            if (last.type.equals(TYPE_PAUSED)) {
                progress = last.progress;
            } else if (last.type.equals(TYPE_STARTED)) {
                progress = last.progress + (int)(System.currentTimeMillis() - last.time);   // 若数据正确就不会溢出
            }
        }
        Log.d(TAG, "getAsyncPlayProgress: " + progress);
        return progress;
    }

    /*
    public int getState(int index) {
        updateAsyncContentList(index);
        if (asyncContentList.isEmpty()) {
            return SHOULD_CONTINUE;
        }
        switch (asyncContentList.get(asyncContentList.size() - 1).type) {
            case TYPE_STARTED:
                return SHOULD_START;
            case TYPE_PAUSED:
                return SHOULD_PAUSE;
            case TYPE_NEXT:
                return SHOULD_NEXT;
        }
        return SHOULD_CONTINUE;
    }
    */

    /**
     * 构造一个基于当前时间以及给定type, index, progress的AsyncContent，并返回其toString的结果
     *
     * @param type 表示这条同步消息是开始/暂停/下一曲播放，可选值: TYPE_STARTED, TYPE_PAUSED, TYPE_NEXT
     * @param index 要同步的曲目在Playlist中的索引
     * @param progress 要同步的曲目播放进度(seekTo, milliseconds)
     * @return 构造出的AsyncContent toString的结果，表示该AsyncContent的json
     */
    public String getCurrentAsyncContentJson(String type, int index, int progress) {

        Gson gson = new Gson();
        long time = System.currentTimeMillis();
        AsyncContent asyncContent = new AsyncContent(type, user, index, progress, time);

        return asyncContent.toString();
    }

    /**
     * 发送同步消息
     * @param type 表示要发送的同步消息是开始/暂停/下一曲播放，可选值: TYPE_STARTED, TYPE_PAUSED, TYPE_NEXT
     * @param index 要同步的曲目在Playlist中的索引
     * @param progress 要同步的曲目播放进度(seekTo, milliseconds)
     */
    private void sendAsyncMsg(String type, int index, int progress) {
        String contentJson = getCurrentAsyncContentJson(type, index, progress);
        Msg msg = new Msg(Msg.TYPE_PLAYER_ASYNC, user, contentJson);

        Log.d(TAG, "sendAsyncMsg: msg: " + msg);

        if (sendingAsyncMsg != null && msg.getType().equals(sendingAsyncMsg.getType())) {
            // 可能是用户快速连点按钮造成的大量相同数据，不重复发送
            return;
        }
        new SendTask(new RequestTaskListener<String>() {
            @Override
            public void onStart() {
                sendingAsyncMsg = msg;
            }

            @Override
            public void onSuccess(String s) {}

            @Override
            public void onFailed(String error) {}

            @Override
            public void onFinish(String s) {
                sendingAsyncMsg = null;
            }
        }).execute(user.getUid(), msg.toString());
    }

    /**
     * 更新同步消息列表，暂时弃用
     */
    private void updateAsyncContentList(int index) {
        // 移除过时的
//        if (currentIndex < index) {
//            for (AsyncContent i : asyncContentList) {
//                if (i.index < index) {
//                    asyncContentList.remove(i);
//                }
//            }
//            currentIndex = index;
//        }
    }

}
