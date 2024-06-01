package com.sreeginy.tairp_weather.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 歌词行
 * 包括该行歌词的时间，歌词内容
 */
public class LrcRow implements Comparable<LrcRow>{
    public final static String TAG = "LrcRow";

    /** 该行歌词要开始播放的时间，格式如下：[02:34.14] */
    public String strTime;

    /** 该行歌词要开始播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：time=02*60*1000+34*1000+14
     */
    public long time;

    /** 该行歌词的内容 */
    public String content;

    public LrcRow(String strTime,long time,String content){
        this.strTime = strTime;
        this.time = time;
        this.content = content;
        //      Log.d(TAG,"strTime:" + strTime + " time:" + time + " content:" + content);
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + strTime + " ]"  + content;
    }

    /**
     * 读取歌词的每一行内容，转换为LrcRow，加入到集合中
     */
    public static List<LrcRow> createRows(String standardLrcLine) {
        try {
            // 判断是否是歌词行
            if (standardLrcLine.indexOf("[") != 0) {
                return null;
            }

            // 找到最后一个 ']' 的位置
            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            // 歌词内容就是 ']' 的位置之后的文本
            String content = standardLrcLine.substring(lastIndexOfRightBracket + 1).trim();

            // 提取歌词时间部分
            String times = standardLrcLine.substring(0, lastIndexOfRightBracket + 1);
            // 通过 '-' 来拆分字符串
            String[] arrTimes = times.split("-");

            List<LrcRow> listTimes = new ArrayList<>();
            for (String temp : arrTimes) {
                if (temp.trim().isEmpty()) {
                    continue;
                }
                // 去除方括号，并转换为统一的时间格式
                temp = temp.replace("[", "").replace("]", "").trim();
                // 创建 LrcRow 对象，并添加到列表中
                LrcRow lrcRow = new LrcRow(temp, timeConvert(temp), content);
                listTimes.add(lrcRow);
            }
            return listTimes;
        } catch (Exception e) {
            Log.e(TAG, "createRows exception:" + e.getMessage());
            return null;
        }
    }


    /**
     * 将解析得到的表示时间的字符转化为Long型
     */
    private static long timeConvert(String timeString) {
        // 如果时间字符串包含小数点
        if (timeString.contains(".")) {
            // 将字符串 XX:XX.XX 或 XX:XX.X 转换为 XX:XX:XX.XX 或 XX:XX:XX.X0 格式
            timeString = timeString.replace('.', ':');
            // 将字符串 XX:XX:XX.XX 或 XX:XX:XX.X0 拆分
            String[] times = timeString.split(":");
            // 如果毫秒的部分只有一位数字，则在末尾添加一个零
            if (times[2].length() == 1) {
                timeString += "0";
            } else if (times[2].length() > 2) {
                timeString = timeString.substring(0, 8);
            }
        } else {
            // 如果时间字符串不包含小数点，则直接转换为 XX:XX:XX 格式
            timeString = timeString.replace('.', ':') + ":00";
        }

        // 将字符串 XX:XX:XX 或 XX:XX:XX.XX 或 XX:XX:XX.X0 转换为毫秒数
        String[] times = timeString.split(":");
        return (long) Integer.parseInt(times[0]) * 60 * 1000 + // 分
                Integer.parseInt(times[1]) * 1000L + // 秒
                Integer.parseInt(times[2]); // 毫秒
    }


    /**
     * 排序的时候，根据歌词的时间来排序
     */
    public int compareTo(LrcRow another) {
        return (int)(this.time - another.time);
    }
}
