package com.mine.musharing.requestTasks;

import com.mine.musharing.bases.Playlist;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

/**
 * <h1>获取一张由 Category 目录提供的 播放列表 的请求与响应解析任务</h1>
 *
 * 若解析成功调用 {@code listener.onSuccess(Playlist result)}，把请求成员成功得到的播放列表(Playlist)传出给listener
 */
public class PlaylistTask extends RequestTask<Playlist> {

    public PlaylistTask(RequestTaskListener<Playlist> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText = RequestUtil.playlist(strings[0], strings[1]);    // uid, playlistId
            Playlist newPlaylist = ParseUtil.playlistResponseParse(responseText);
            if (newPlaylist == null) {
                newPlaylist = new Playlist();
            }

            listener.onSuccess(newPlaylist);
            return RequestTask.REQUEST_SUCCESSFUL;

        } catch (ParseUtil.ResponseError e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
        return RequestTask.REQUEST_FAILED;
    }
}
