package com.jason.bing.handler;

import com.jason.bing.RecognizeResponse;
import com.jason.bing.WordInfo;
import com.jason.bing.util.WordUtil;
import org.apache.log4j.Logger;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/29
 */
public class AudioFileRecognizationHandler extends BingAudioFileHandler {

    private static final Logger LOGGER = Logger.getLogger(AudioFileRecognizationHandler.class);



    @Override
    public void onSpeechPhrase(RecognizeResponse response) {
        WordInfo wordInfo = WordUtil.parsePhrase(response.getBodyEntity());
//        LOGGER.info("--- onSpeechPhrase --- : " + JSONObject.toJSONString(wordInfo));
        doOnSpeechPhrase(wordInfo);
    }



}
