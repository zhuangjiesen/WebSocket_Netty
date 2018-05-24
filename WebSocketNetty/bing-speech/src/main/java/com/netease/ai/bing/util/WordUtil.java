package com.netease.ai.bing.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.ai.bing.WordInfo;

import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/22
 */
public class WordUtil {


    public static WordInfo parseFragment(String body) {
        WordInfo wordInfo = WordUtil.parseFragment(JSON.parseObject(body));
        return wordInfo;
    }


    public static WordInfo parseFragment(JSONObject bodyEntity) {
        WordInfo wordInfo = new WordInfo();
        int offset = bodyEntity.getIntValue("Offset");
        int duration = bodyEntity.getIntValue("Duration");
        String text = bodyEntity.getString("Text");

        double startTime = (double)offset / 10000000;
        String startNanosPart = String.valueOf(startTime);
        startNanosPart = startNanosPart.substring(startNanosPart.indexOf("."));
        long start = (long)startTime;
        String startStr = DateUtil.secToTime(start).concat(startNanosPart);

        int endset = offset + duration;
        double endTime = (double)endset / 10000000;
        String endNanosPart = String.valueOf(endTime);
        endNanosPart = endNanosPart.substring(endNanosPart.indexOf("."));
        long end = (long)endTime;
        String endStr = DateUtil.secToTime(end).concat(endNanosPart);
        wordInfo.setDuration(duration);
        wordInfo.setEndTime(endStr);
        wordInfo.setStartTime(startStr);
        wordInfo.setText(text);
        wordInfo.setStartTimeNum(offset);
        wordInfo.setEndTimeNum(endset);
        return wordInfo;
    }



    public static WordInfo parsePhrase(String body) {
        WordInfo wordInfo = WordUtil.parsePhrase(JSON.parseObject(body));
        return wordInfo;
    }


    public static void resetTime(WordInfo wordInfo) {
        int offset = wordInfo.getStartTimeNum();
        int duration = wordInfo.getDuration();

        double startTime = (double)offset / 10000000;
        String startNanosPart = String.valueOf(startTime);
        startNanosPart = startNanosPart.substring(startNanosPart.indexOf("."));
        long start = (long)startTime;
        String startStr = DateUtil.secToTime(start).concat(startNanosPart);
        int endset = offset + duration;
        double endTime = (double)endset / 10000000;
        String endNanosPart = String.valueOf(endTime);
        endNanosPart = endNanosPart.substring(endNanosPart.indexOf("."));
        long end = (long)endTime;
        String endStr = DateUtil.secToTime(end).concat(endNanosPart);

        wordInfo.setDuration(duration);
        wordInfo.setEndTime(endStr);
        wordInfo.setStartTime(startStr);
        wordInfo.setStartTimeNum(offset);
        wordInfo.setEndTimeNum(endset);
    }


    public static WordInfo parsePhrase(JSONObject bodyEntity) {
        WordInfo wordInfo = null;

        String recognitionStatus = bodyEntity.getString("RecognitionStatus");
        if ("Success".equals(recognitionStatus)) {
            wordInfo = new WordInfo();
            String text = bodyEntity.getString("DisplayText");
            int offset = bodyEntity.getIntValue("Offset");
            int duration = bodyEntity.getIntValue("Duration");
            double startTime = (double)offset / 10000000;
            String startNanosPart = String.valueOf(startTime);
            startNanosPart = startNanosPart.substring(startNanosPart.indexOf("."));
            long start = (long)startTime;
            String startStr = DateUtil.secToTime(start).concat(startNanosPart);
            int endset = offset + duration;
            double endTime = (double)endset / 10000000;
            String endNanosPart = String.valueOf(endTime);
            endNanosPart = endNanosPart.substring(endNanosPart.indexOf("."));
            long end = (long)endTime;
            String endStr = DateUtil.secToTime(end).concat(endNanosPart);

            wordInfo.setDuration(duration);
            wordInfo.setEndTime(endStr);
            wordInfo.setStartTime(startStr);
            wordInfo.setText(text);
            wordInfo.setStartTimeNum(offset);
            wordInfo.setEndTimeNum(endset);
        }
        return wordInfo;
    }


}
