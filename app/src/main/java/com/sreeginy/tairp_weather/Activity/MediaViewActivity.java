package com.sreeginy.tairp_weather.Activity;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;

public class MediaViewActivity extends ViewModel {
    public ArrayList<String> mediaList = new ArrayList<String>();
    public ArrayList<String> updateSongManage(String Path){
        mediaList.clear();
        File file = new File(Path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    if (item.getName().endsWith(".mp3")) {
                        mediaList.add(item.getName());
                    }
                }
            }
        }
        return mediaList;
    }

    public String getLyrics(String songName){
        return "";
    }
    public String calculateTime(int time) {
        int minute;
        int second;
        if (time >= 60) {
            minute = time / 60;
            second = time % 60;
            //分钟在0~9
            if (minute < 10) {
                //判断秒
                if (second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "0" + minute + ":" + second;
                }
            } else {
                //分钟大于10再判断秒
                if (second < 10) {
                    return minute + ":" + "0" + second;
                } else {
                    return minute + ":" + second;
                }
            }
        } else {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        }
    }

}
