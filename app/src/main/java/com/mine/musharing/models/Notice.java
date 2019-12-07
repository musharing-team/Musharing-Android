package com.mine.musharing.models;

public class Notice {

    String nid;

    String title;

    String content;

    public Notice(String nid, String title, String content) {
        this.nid = nid;
        this.title = title;
        this.content = content;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
