package com.mine.musharing.utils;

import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeScroll;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <h1>小工具库</h1>
 * <p>
 * 一些小的工具
 */
public class Utility {

    /**
     * 把 {@code byte[]} 转换成 <em>十六进制</em> 表示<br/>
     * <p>
     * 用来支持 encryptPassword 时，java 获取的是一个 byte[], 把它转换成后端需要的 十六进制 表示。
     *
     * @param bytes 待转化的byte[]
     * @return 转化出的十六进制字符串表示
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * <h2>格式化文本</h2>
     * <p>
     * 用来解决显示的在对话气泡中的文本太多可能超出屏幕的问题<br/>
     * 实现方式为在s中每singleLineLong个字符后插入一个"\n"
     *
     * @param s              待格式化的字符串
     * @param singleLineLong 最大单行长度
     * @return 格式化后的字符串
     */
    public static String formatText(String s, int singleLineLong) {
        StringBuilder stringBuilder = new StringBuilder();

        int i;
        for (i = 0; i + singleLineLong < s.length(); i += singleLineLong) {
            stringBuilder.append(s.substring(i, i + singleLineLong));
            stringBuilder.append("\n");
        }
        stringBuilder.append(s.substring(i));

        return stringBuilder.toString();
    }

    /**
     * <h2>格式化歌曲进度</h2>
     * <p>
     * 把从 MediaPlayer 获取的 progress in milliseconds 格式化成可读的时间(e.g. 03:11)
     *
     * @param progress playing progress in milliseconds
     * @return formated time string
     */
    public static String formatMusicProgress(int progress) {
        String sign = "";
        if (progress < 0) {
            sign = "-";
            progress = -progress;
        }
        long t = progress / 1000;
        long m = t / 60;
        long s = t % 60;
        StringBuilder minute = new StringBuilder();
        if (m < 10) {
            minute.append("0");
        }
        minute.append(m);
        StringBuilder second = new StringBuilder();
        if (s < 10) {
            second.append("0");
        }
        second.append(s);
        return sign + minute.toString() + ":" + second.toString();
    }

    /**
     * <h2>为卡片获取随机背景颜色</h2>
     * <p>
     * 例如: cardView.setCardBackgroundColor(Utility.randomCardColor());
     *
     * @return 颜色的值
     */
    public static int randomCardColor() {
        int lastCardColorIndex = 0;
        List<Integer> colors = new ArrayList<>();
        colors.add(0xffddeaf3);
        colors.add(0xfff9ede9);
        colors.add(0xffb6eee1);
        colors.add(0xfffaead2);
        colors.add(0xffd1e3e1);
        colors.add(0xffeaede1);
        colors.add(0xffdee2ed);
        colors.add(0xffcef0e1);
        colors.add(0xffc9e5e8);
        colors.add(0xffe3dbfa);
        colors.add(0xffffdffb);
        Random random = new Random();
        int i;
        for (i = 0; i == lastCardColorIndex; i = random.nextInt(colors.size())) {
            continue;
        }
        lastCardColorIndex = i;
        return colors.get(i);
    }


    /**
     * 去除字符串中头部和尾部所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
     *
     * @param s
     * @return
     */
    public static String stringTrim(String s) {
        String result = "";
        if (null != s && !"".equals(s)) {
            result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
        }
        return result;
    }

    /**
     * 获取一组随即转场动画
     *
     * @return a random TransitionSet
     */
    public static TransitionSet getRandomTransitionSet() {
        TransitionSet transitionSet = new TransitionSet();

        transitionSet.addTransition(new ChangeBounds());
        transitionSet.addTransition(new ChangeClipBounds());

        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new ChangeImageTransform());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            transitionSet.addTransition(new ChangeScroll());
        }
        // randomly apply one or all:
        switch (new Random().nextInt(4)) {
            case 0:
                transitionSet.addTransition(new Fade());
                break;
            case 1:
                transitionSet.addTransition(new Slide());
                break;
            case 2:
                transitionSet.addTransition(new Explode());
                break;
            default:
                transitionSet.addTransition(new Fade());
                transitionSet.addTransition(new Slide());
                transitionSet.addTransition(new Explode());
        }
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);

        return transitionSet;
    }
}