package com.mine.musharing.requestTasks;

import com.mine.musharing.bases.Playlist;
import com.mine.musharing.utils.ParseUtil;
import com.mine.musharing.utils.RequestUtil;

public class PlaylistTask extends RequestTask<Playlist> {

    public PlaylistTask(RequestTaskListener<Playlist> listener) {
        super(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String responseText = RequestUtil.playlist(strings[0], strings[1]);    // uid, playlistId
            Playlist newPlaylist = ParseUtil.playlistContentParse(responseText);
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
