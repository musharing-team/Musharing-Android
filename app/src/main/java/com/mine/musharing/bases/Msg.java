package com.mine.musharing.bases;


import com.google.gson.Gson;

/**
 * <h1>消息类</h1>
 */
public class Msg {

    /**
     * 消息类型：纯文本消息
     */
    public static final String TYPE_TEXT = "text";

    /**
     * 消息类型：播放列表
     */
    public static final String TYPE_PLAYLIST = "playlist";

    /**
     * 消息类型: 同步信息
     */
    public static final String TYPE_PLAYER_ASYNC = "player_async";

    /**
     * 消息类型： 语言消息
     */
    public static final String TYPE_RECORD = "record";

    /**
     * 消息类型: void，可能意外发出的空消息
     */
    public static final String TYPE_VOID = "void";

    /**
     * 消息类型
     */
    protected String type;

    /**
     * 发送者uid
     */
    protected String fromUid;

    /**
     * 发送着用户名
     */
    protected String fromName;

    /**
     * 发送者头像图片url
     */
    protected String fromImg;

    /**
     * 消息的具体内容
     */
    protected String content;

    public Msg() {
        this.type = TYPE_VOID;
    }

    public Msg(String type, User fromUser, String content) {
        this.type = type;
        this.fromUid = fromUser.getUid();
        this.fromName = fromUser.getName();
        this.fromImg = fromUser.getImgUrl();
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromImg() {
        return fromImg;
    }

    public void setFromImg(String fromImg) {
        this.fromImg = fromImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 将Msg对象转化为json格式的字符串
     * @return json格式的字符串
     */
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
