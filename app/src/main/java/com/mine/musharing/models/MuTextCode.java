package com.mine.musharing.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MuTextCode: Mu口令
 */
public class MuTextCode {
    private final static String muTextCodePattern = "复制Mu口令，打开Musharing添加朋友，即可加入我的房间【(.*?)】";

    /**
     * 生成并返回给定用户的 Mu 口令
     */
    public static String make(User user) {
        return muTextCodePattern.replace("(.*?)", user.getName());
    }

    /**
     * 解析 Mu 口令，若解析成功 ，则返回 targetName，否则返回 ""
     */
    public static String parse(String code) {
        Pattern r = Pattern.compile(muTextCodePattern);
        Matcher m = r.matcher(code);
        if (m.find()) {
            return m.group(1);     // targetName
        }
        return "";
    }
}
