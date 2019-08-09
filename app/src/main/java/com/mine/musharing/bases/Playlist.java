package com.mine.musharing.bases;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * <h1>播放列表类</h1>
 *
 * <p>继承自Msg，可toString成Msg JSON，可以当作Msg发送、接收</p>
 */
public class Playlist extends Msg implements Serializable {

    private String id;

    private List<Music> musicList;

    private int size;

    private long totalDuration;

    public Playlist(User fromUser) {
        super(Msg.TYPE_PLAYLIST, fromUser, "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public int getSize() {
        return size;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void commit() {
        id = UUID.randomUUID().toString();
        size = musicList.size();
        totalDuration = 0;

        JSONArray musicArrayJson = new JSONArray();
        for (Music music : musicList) {
            totalDuration += music.getDuration();
            musicArrayJson.put(music);
        }

        JSONObject contentJson = new JSONObject();
        try {
            contentJson.put("id", id);
            contentJson.put("size", size);
            contentJson.put("total_duration", totalDuration);
            contentJson.put("music_list", musicArrayJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        content = contentJson.toString();
    }
}
