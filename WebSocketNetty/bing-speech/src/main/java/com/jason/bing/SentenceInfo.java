package com.jason.bing;

import java.util.ArrayList;
import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: wordInfo 的集合，一个句子
 * @Date: Created in 2018/5/22
 */
public class SentenceInfo {
    private List<WordInfo> wordInfoList;
    public SentenceInfo() {
        wordInfoList = new ArrayList<>();
    }

    public int getWordSize () {
        return wordInfoList.size();
    }


    public void addWordInfo (WordInfo wordInfo) {
        wordInfoList.add(wordInfo);
    }


    public WordInfo getLatestWord() {
        if (wordInfoList.size() == 0) {
            return null;
        }
        return wordInfoList.get(wordInfoList.size() - 1);
    }

    /*
     *
     * 带时间轴的结果
     * @author zhuangjiesen
     * @date 2018/5/29 下午4:12
     * @param
     * @return
     */
    public String getSentence() {
        if (wordInfoList.size() > 0) {

            String startTime = wordInfoList.get(0).getStartTime();
            String endTime = wordInfoList.get(wordInfoList.size() - 1).getEndTime();
            StringBuilder textSb = new StringBuilder();
            for (WordInfo item : wordInfoList) {
                //首字母不用空格
                if (textSb.length() > 0) {
                    textSb.append(" ");
                }
                textSb.append(item.getText());
            }
            textSb.append(".");
            return String.format("%s --> %s - %s" , startTime , endTime , textSb.toString());
        }
        return null;
    }


    /*
     *
     * 获取字幕数据
     * @author zhuangjiesen
     * @date 2018/5/30 下午2:57
     * @param
     * @return
     */
    public String getCaption(int seq){
        if (wordInfoList.size() > 0) {
            String startTime = wordInfoList.get(0).getStartTime();
            String endTime = wordInfoList.get(wordInfoList.size() - 1).getEndTime();
            StringBuilder textSb = new StringBuilder();
            textSb.append(seq);
            textSb.append(SpeechEventConstant.LINE_SEP);
            textSb.append(startTime);
            textSb.append(" --> ");
            textSb.append(endTime);
            textSb.append(SpeechEventConstant.LINE_SEP);
            for (WordInfo item : wordInfoList) {
                //首字母不用空格
                if (textSb.length() > 0) {
                    textSb.append(" ");
                }
                textSb.append(item.getText());
            }
            textSb.append(".");
            textSb.append(SpeechEventConstant.LINE_SEP);
            textSb.append(SpeechEventConstant.LINE_SEP);
            return textSb.toString();
        }
        return null;

    }




    /*
     *
     * 获取字幕模板数据
     * @author zhuangjiesen
     * @date 2018/5/30 下午2:57
     * @param
     * @return
     */
    public String getCaptionTemplate(){
        if (wordInfoList.size() > 0) {
//            String startTime = wordInfoList.get(0).getStartTime();
            double start = (double)wordInfoList.get(0).getStartTimeNum() / 10000000;
//            String endTime = wordInfoList.get(wordInfoList.size() - 1).getEndTime();
            double end = (double)wordInfoList.get(wordInfoList.size() - 1).getEndTimeNum()/ 10000000;
            StringBuilder textSb = new StringBuilder();
            textSb.append("[");
            textSb.append(start);
            textSb.append("]");
            for (WordInfo item : wordInfoList) {
                //首字母不用空格
                if (textSb.length() > 0) {
                    textSb.append(" ");
                }
                textSb.append(item.getText());
            }
            textSb.append(".");
            textSb.append("[");
            textSb.append(end);
            textSb.append("] ");
           return textSb.toString();
        }
        return null;

    }




    /*
     *
     * 不带时间轴
     * @author zhuangjiesen
     * @date 2018/5/29 下午4:12
     * @param
     * @return
     */
    public String getText(){
        if (wordInfoList.size() > 0) {
            StringBuilder textSb = new StringBuilder();
            for (WordInfo item : wordInfoList) {
                //首字母不用空格
                if (textSb.length() > 0) {
                    textSb.append(" ");
                }
                textSb.append(item.getText());
            }
            return textSb.toString();
        }
        return null;
    }


    public List<WordInfo> getWordInfoList() {
        return wordInfoList;
    }

    public void setWordInfoList(List<WordInfo> wordInfoList) {
        this.wordInfoList = wordInfoList;
    }
}
