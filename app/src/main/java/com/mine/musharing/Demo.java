package com.mine.musharing;

import com.mine.musharing.models.Music;
import com.mine.musharing.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Demo 版本中未实现部分的临时实例
 * 现已不再需要
 */
public class Demo {

    public static User su = new User("0", "su", "https://cdn.pixabay.com/photo/2014/04/03/11/58/plant-312737__480.png");

    public static Music music01 = new Music("test01", "1", "Unknown", "None", "https://www.baidu.com/img/bd_logo1.png?qua=high", 1000, "http://cr0123.gz01.bdysite.com/file/1.mp3");
    public static Music music02 = new Music("test02", "music", "Unknown", "None", "https://support.apple.com/content/dam/edam/applecare/images/en_US/homepage/featured-section-icloud_2x.png", 200000, "http://cr0123.gz01.bdysite.com/file/music.mp3");

    public static Music[] life_BaSO4 = new Music[]{
            new Music("BaSO4_Life_01", "Need For Relaxation", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/Need%20For%20Relaxation.mp3"),
            new Music("BaSO4_Life_02", "命运之石", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/命运之石.mp3"),
            new Music("BaSO4_Life_03", "四伏", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/四伏.m4a"),
            new Music("BaSO4_Life_04", "随心所欲", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/随心所欲.m4a"),
            new Music("BaSO4_Life_05", "分歧", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/分歧.mp3"),
            new Music("BaSO4_Life_06", "潮湿", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/潮湿.m4a"),
            new Music("BaSO4_Life_07", "逆风", "BaSO4", "Life", "http://cr0123.gz01.bdysite.com/file/Life/Life.png", 0, "http://cr0123.gz01.bdysite.com/file/Life/逆风.m4a"),

    };

    public static List<Music> testMusicList = new ArrayList<>(Arrays.asList(life_BaSO4));   // Read this [reference](https://stackoverflow.com/questions/7399482/java-lang-unsupportedoperationexception-at-java-util-abstractlist-removeunknown).

}
