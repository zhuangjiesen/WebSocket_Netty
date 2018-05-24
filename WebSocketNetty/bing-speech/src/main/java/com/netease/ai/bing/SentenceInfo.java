package com.netease.ai.bing;

import java.util.ArrayList;
import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
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

    public List<WordInfo> getWordInfoList() {
        return wordInfoList;
    }

    public void setWordInfoList(List<WordInfo> wordInfoList) {
        this.wordInfoList = wordInfoList;
    }
}
