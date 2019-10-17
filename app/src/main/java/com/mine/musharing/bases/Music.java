package com.mine.musharing.bases;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * <h1>音乐类</h1>
 *
 * <p>表示一首音乐，包含其 识别id、歌名、作者、唱片、唱片封面图地址、时长、音频文件地址</p>
 */
public class Music implements Serializable {

    private String id;

    private String name;

    private String artist;

    private String album;

    private String AlbumImageUrl;

    private long duration;

    private String fileUrl;

    public static Music voidMusic = new Music("void", " ", " ", " ", "...", 0, "...");

    public Music() {
    }

    public Music(String id, String name, String artist, String album, String albumImageUrl, long duration, String fileUrl) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        AlbumImageUrl = albumImageUrl;
        this.duration = duration;
        this.fileUrl = fileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumImageUrl() {
        return AlbumImageUrl;
    }

    public void setAlbumImageUrl(String albumImageUrl) {
        AlbumImageUrl = albumImageUrl;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 将Music对象转化为json格式的字符串
     * @return json格式的字符串
     */
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
