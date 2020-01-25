package com.mine.musharing.utils;

import com.mine.musharing.models.User;

/**
 * <h1>状态库</h1>
 *
 * 用来保存 app 全局的状态，例如当前登录的 user。
 */
public class StatusUtil {

    /**
     * 当前登录的用户（暂时没有使用，现在的解决方案是在各个Activity、Fragment 间用 intent bundle 传递）
     */
    public static User user = null;

    /**
     * 是否启用 chatbot
     */
    public static boolean chatbotEnable = false;
}
