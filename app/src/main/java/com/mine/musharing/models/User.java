package com.mine.musharing.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * <h1>用户类</h1>
 */
public class User implements Serializable {

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户头像url地址
     */
    private String imgUrl;

    public User(String uid, String name, String imgUrl) {
        this.uid = uid;
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    /**
     * 以 uid 是否相同为标准衡量两个 User 对象相同与否
     * @param o other User Object
     * @return true if {@code this.uid.equals(o.uid)} else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return uid.equals(user.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "{" +
                "\"uid=\":\"" + uid + '\"' +
                ", \"name\":\"" + name + '\"' +
                ", \"imgUrl\"" + imgUrl + '\"' +
                '}';
    }
}
