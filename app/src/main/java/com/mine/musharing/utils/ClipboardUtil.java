package com.mine.musharing.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

    /**
     * 将给定文本放入剪贴板，成功则返回 true，否则返回 false
     *
     * @param clipboard 剪贴板实例，需要从 Activity 中获取
     * @param content 要放入的文本
     * @return true if success
     */
    public static boolean setToClipboard(ClipboardManager clipboard, String content) {
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("musharing", content);
            clipboard.setPrimaryClip(clip);
            return true;
        }
        return false;
    }

    /**
     * 从剪贴板中获取文本内容
     *
     * @param clipboard 剪贴板实例，需要从 Activity 中获取
     * @return 成功则返回获取到的内容，否则返回空字符串""
     */
    public static String getFromClipboard(ClipboardManager clipboard) {
        if ((clipboard != null) && (clipboard.getPrimaryClip() != null)) {
            return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        }
        return "";
    }

}
