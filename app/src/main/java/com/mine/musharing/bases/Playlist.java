package com.mine.musharing.bases;

import com.mine.musharing.utils.UserUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <h1>播放列表类</h1>
 *
 * <p>继承自Msg，可toString成Msg JSON，可以当作Msg发送、接收</p>
 */
public class Playlist extends Msg implements Serializable {

    private String id;

    private List<Music> musicList = new ArrayList<>();  // 初始化以防止列表在没有完成从服务器下载时被调用导致 a null object reference

    private int size;

    private long totalDuration;

    public Playlist() {
        super(Msg.TYPE_PLAYLIST, UserUtil.playlistFakeUser, "");
        this.setType(Msg.TYPE_PLAYLIST);
        this.setFromUser(UserUtil.playlistFakeUser);
        this.setContent("");
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

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String commit() {
        id = UUID.randomUUID().toString();
        size = musicList.size();
        totalDuration = 0;

        try {
            JSONArray musicArrayJson = new JSONArray();
            for (Music music : musicList) {
                totalDuration += music.getDuration();
                JSONObject musicJson = new JSONObject(music.toString());
                musicArrayJson.put(musicJson);
            }

            JSONObject contentJson = new JSONObject();
            contentJson.put("id", id);
            contentJson.put("size", size);
            contentJson.put("total_duration", totalDuration);
            contentJson.put("music_list", musicArrayJson);

            this.setContent(contentJson.toString());

            return id;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}
