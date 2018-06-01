package com.jason.bing.handler;

import com.jason.bing.AbstractRecognizeEventListener;
import com.jason.bing.RecognizeResponse;
import com.jason.bing.SentenceInfo;
import com.jason.bing.WordInfo;
import com.jason.bing.util.WordUtil;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 通用的音频文件识别触发事件处理类
 * @Date: Created in 2018/6/1
 */
public class BingAudioFileHandler extends AbstractRecognizeEventListener {

    private static final Logger LOGGER = Logger.getLogger(BingAudioFileHandler.class);


    protected SentenceInfo currentSentenceInfo = new SentenceInfo();



    /*
     * 重新设置时间轴
     * @author zhuangjiesen
     * @date 2018/6/1 上午12:45
     * @param
     * @return
     */
    protected void doOnSpeechPhrase(WordInfo wordInfo) {
        if (wordInfo == null) {
            return;
        }
        long startTime = wordInfo.getStartTimeNum();
        if (currentSentenceInfo == null) {
            return;
        }
        //重置时间，每段的时间轴都是从0 开始
        List<WordInfo> fragmentList = currentSentenceInfo.getWordInfoList();
        if (fragmentList != null) {
            for (WordInfo fragmentItem : fragmentList) {
                long startTimeOld = fragmentItem.getStartTimeNum();
                fragmentItem.setStartTimeNum(startTimeOld + startTime);
                WordUtil.resetTime(fragmentItem);
            }
        }

        int wordSize = currentSentenceInfo.getWordSize();
        //句子中单词数太多。重新分词
        if (wordSize > wordSizeLimit) {
            fragmentList = currentSentenceInfo.getWordInfoList();
            SentenceInfo newSentenceInfo = new SentenceInfo();
            if (fragmentList.size() == 1) {
                newSentenceInfo.addWordInfo(fragmentList.get(0));
            } else {
                for (int i = 1 ; i < fragmentList.size() ; i++) {
                    WordInfo lastWord = null;
                    if ((lastWord = newSentenceInfo.getLatestWord())== null) {
                        lastWord = fragmentList.get(i - 1);
                        newSentenceInfo.addWordInfo(lastWord);
                    }
                    WordInfo currentWord = fragmentList.get(i);

                    if (currentWord.getStartTimeNum() == 0) {
                        //等于新的一句 开始
                        newSentenceInfo = new SentenceInfo();
                        newSentenceInfo.addWordInfo(currentWord);
                    } else if ((currentWord.getStartTimeNum() - lastWord.getEndTimeNum()) > breakMSecond) {
                        //等于新的一句 开始
                        newSentenceInfo = new SentenceInfo();
                        newSentenceInfo.addWordInfo(currentWord);
                    } else {
                        newSentenceInfo.addWordInfo(currentWord);
                    }
                }
            }
        }
        currentSentenceInfo = new SentenceInfo();
    }



    @Override
    public void onSpeechFragment(RecognizeResponse response) {
        WordInfo wordInfo = WordUtil.parseFragment(response.getBodyEntity());
        if (wordInfo == null) {
            return ;
        }
        currentSentenceInfo.addWordInfo(wordInfo);
    }




}
