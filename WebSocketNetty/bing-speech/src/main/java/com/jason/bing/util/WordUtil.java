package com.jason.bing.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jason.bing.WordInfo;


/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 消息解析工具类
 * @Date: Created in 2018/5/22
 */
public class WordUtil {


    public static WordInfo parseFragment(String body) {
        WordInfo wordInfo = WordUtil.parseFragment(JSON.parseObject(body));
        return wordInfo;
    }


    public static WordInfo parseFragment(JSONObject bodyEntity) {
        WordInfo wordInfo = new WordInfo();
        long offset = bodyEntity.getLongValue("Offset");
        int duration = bodyEntity.getIntValue("Duration");
        String text = bodyEntity.getString("Text");

        double startTime = (double)offset / 10000000;
        String startNanosPart = String.valueOf(startTime);
        startNanosPart = startNanosPart.substring(startNanosPart.indexOf("."));
        long start = (long)startTime;
        String startStr = DateUtil.secToTime(start).concat(startNanosPart);

        long endset = offset + duration;
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
        long offset = wordInfo.getStartTimeNum();
        int duration = wordInfo.getDuration();

        double startTime = (double)offset / 10000000;
        String startNanosPart = String.valueOf(startTime);
        startNanosPart = startNanosPart.substring(startNanosPart.indexOf("."));
        long start = (long)startTime;
        String startStr = DateUtil.secToTime(start).concat(startNanosPart);
        long endset = offset + (long)duration;
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
        return parsePhrase(bodyEntity , 0);
    }




    /*
     *
     *
     * @author zhuangjiesen
     * @date 2018/5/31 下午11:14
     * @param  startTime 起始时间，长文本的语音识别，被切割成多个文件，时间轴需要叠加 单位：秒
     * @return
     */
    public static WordInfo parsePhrase(JSONObject bodyEntity , long startTimePrevousSec) {
        WordInfo wordInfo = null;

        String recognitionStatus = bodyEntity.getString("RecognitionStatus");
        if ("Success".equals(recognitionStatus)) {
            wordInfo = new WordInfo();
            String text = bodyEntity.getString("DisplayText");
            long offset = bodyEntity.getLongValue("Offset") + startTimePrevousSec * 10000000;
            int duration = bodyEntity.getIntValue("Duration");
            double startTime = (double)offset / 10000000;
            String startNanosPart = String.valueOf(startTime);
            startNanosPart = startNanosPart.substring(startNanosPart.indexOf("."));
            long start = (long)startTime;
            String startStr = DateUtil.secToTime(start).concat(startNanosPart);
            long endset = (long) offset + (long) duration;
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
