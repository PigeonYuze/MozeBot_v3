package com.pigeonyuze.command.phigros.data;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 对Phigros的歌曲进行查找
 * @see #querySongByDifficulty(Integer, double, boolean)  根据定数查找歌曲名
 * @see #getSongTextData(String) 获取指定歌曲的内容
 * @see #getSongImageFileOrNull(String) 获取指定歌曲的曲绘
 * */
public class PhigrosSongUtils {

    public static final PhigrosSongUtils INSTANCE = new PhigrosSongUtils();

    @Deprecated
    private final String path = "D:\\IdeaProjects\\0-java\\MozeBot\\src\\main\\resources\\pgr\\phigrosData.json";

    public static void test(){
        PhigrosSongUtils.class.getResource("");
    }
    /**
     * 查找指定定数的歌曲名
     * @param category 查找的等级
     *        <br/>代表的值:
     *        <br/>1 - EZ
     *        <br/>2 - HD
     *        <br/>3 - IN
     *        <br/>4 - AT
     *        <br/>5 - Legacy
     *        <br/> 可以传值null 如果为null将为查找所有等级
     * @param difficulty 需要查找的定数 （不可为0）
     * @param isStrictMatch 是否为严格判断
     *                      <br/>如果为严格判断（true） 则当等于时才会返回值
     *                      <br/>如果为宽松判断（false） 则大于 (difficulty) 小于 (difficulty+1) 的都会返回
     * @return 查找到的歌曲名(可为空组)
     * @see #querySongFullName(String) 获取别称对应的原名称
     * @see #getSongImageFileOrNull(String)  获取歌曲曲绘
     * @see #getSongTextData(String)  获取歌曲内容（介绍）
     * */
    public List<PhigrosSong> querySongByDifficulty(Integer category, double difficulty, boolean isStrictMatch) {
        List<PhigrosSong> dataGetName = new ArrayList<>();
        for (PhigrosSong phigrosSong : PhigrosSongManager.PHIGROS_SONG_LIST) {
            double IN = phigrosSong.getInLevel().getDestiny();
            double HD = phigrosSong.getHdLevel().getDestiny();
            double EZ = phigrosSong.getEzLevel().getDestiny();
            double AT = phigrosSong.isAtLevelExists() ? phigrosSong.getAtLevel().getDestiny() : -1.0D;
            switch (category) {
                case 1: {
                    if (isStrictMatch && EZ == difficulty) dataGetName.add(phigrosSong);
                    if (!isStrictMatch && (EZ >= difficulty && EZ < difficulty + 0.6)) dataGetName.add(phigrosSong);
                }
                case 2: {
                    if (isStrictMatch && HD == difficulty) dataGetName.add(phigrosSong);
                    if (!isStrictMatch && (HD >= difficulty && HD < difficulty + 0.6)) dataGetName.add(phigrosSong);
                }
                case 3: {
                    if (isStrictMatch && IN == difficulty) dataGetName.add(phigrosSong);
                    if (!isStrictMatch && (IN >= difficulty && IN < difficulty + 0.6)) dataGetName.add(phigrosSong);
                }
                case 4: {
                    if (isStrictMatch && AT == difficulty) dataGetName.add(phigrosSong);
                    if (!isStrictMatch && (AT >= difficulty && AT < difficulty + 0.6)) dataGetName.add(phigrosSong);
                }
            }
        }
        return dataGetName;
    }
    /**
     * @deprecated use {@link PhigrosSongManager#randomSong()}
     * */
    @Deprecated
    public String randomSongName(){
        return null;
    }
    /**
     * 根据传来的参数传来可以找到的歌曲原名
     * <br/> 注意: 返回的值是唯一的 如果不是唯一的则不会返回名称
     * <br/>   支持的范围:(不区分大小写)
     * <br/>   -歌曲原名的前几位（不重复）
     * <br/>   -json内支持的别称
     * <br/>   -原名
     * @return 原名
     * @param songName 需要查找的字符串
     * */
    public String querySongFullName(String songName){
        return PhigrosSongManager.findSongOrNullObject(songName).getMasterName();
    }
    /**
     * 匹配
     * */
    public boolean isRoughMatch(@NotNull String strA, @NotNull String strB, int matchThreshold){
        // FIXME: 2022/9/30 错误匹配
        int compLength = Math.min(strA.length(),strB.length());
        int cnt = 0;
        for (int i = 0; i < compLength; i++) {
            if(strA.charAt(i)==strB.charAt(i)){
                cnt++;
            }
            if(cnt == matchThreshold){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取歌曲信息(from Json)
     * @param songName 要查找的歌曲名字<br/>必须是原名称（即初始名称）
     * @return 该歌曲的信息(SongData)<br/>内容基础格式如下<br/>
     *-------------------
     *<br/>Name
     *<br/>章节：Name.Chapter
     *<br/>BPM：Name.BPM
     *<br/>曲师：Song.author
     *<br/>曲绘画师：Song.artist
     *<br/>IN：
     *<br/>IN Chart :
     *<br/>HD：
     *<br/>HD Chart :
     *<br/>EZ：
     *<br/>EZ Chart :
     *<br/>解锁方式：
     *<br/>来源：
     * */
    public String getSongTextData(String songName){
        if(songName != null) {
            return PhigrosSongManager.findSongOrNullObject(songName).toString();
        }else {
            return "输入的值为\"null\"没有找到对应的歌曲详情";
        }
    }
    /**
     * 获取指定歌曲的曲绘文件
     * @param songName 必须为原始名称
     * */
    public File getSongImageFileOrNull(@NotNull String songName){
        try {
            return new File(PhigrosSongManager.findSongOrNullObject(songName).getIllustrationPath());
        } catch (Exception e) {
            return null;
        }

    }


    private static boolean RoughMatch(String strA,String strB,int matchThreshold){
        return new PhigrosSongUtils().isRoughMatch(strA,strB,matchThreshold);
    }
}
