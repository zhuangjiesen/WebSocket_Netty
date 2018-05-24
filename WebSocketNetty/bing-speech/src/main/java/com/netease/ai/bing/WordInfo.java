package com.netease.ai.bing;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/22
 */
public class WordInfo {

    private String text;
    private String startTime;
    // 单位 ： 100纳秒
    private int startTimeNum;
    private String endTime;
    // 单位 ： 100纳秒
    private int endTimeNum;
    private Integer duration;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public int getStartTimeNum() {
        return startTimeNum;
    }

    public void setStartTimeNum(int startTimeNum) {
        this.startTimeNum = startTimeNum;
    }

    public int getEndTimeNum() {
        return endTimeNum;
    }

    public void setEndTimeNum(int endTimeNum) {
        this.endTimeNum = endTimeNum;
    }


    @Override
    public String toString() {
//        00:00:29,850 --> 00:00:30,450 - would you like
        String text = String.format("%s --> %s - %s" , this.startTime , this.endTime , this.text);
        return text;
//        return "WordInfo{" +
//                "text='" + text + '\'' +
//                ", startTime='" + startTime + '\'' +
//                ", startTimeNum=" + startTimeNum +
//                ", endTime='" + endTime + '\'' +
//                ", endTimeNum=" + endTimeNum +
//                ", duration=" + duration +
//                '}';
    }
}
