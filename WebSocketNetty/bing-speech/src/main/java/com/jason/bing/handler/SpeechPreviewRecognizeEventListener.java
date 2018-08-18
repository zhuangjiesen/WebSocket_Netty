package com.jason.bing.handler;

import com.jason.bing.AbstractRecognizeEventListener;
import com.jason.bing.RecognizeResponse;
import org.apache.log4j.Logger;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/6/19
 */
public class SpeechPreviewRecognizeEventListener extends AbstractRecognizeEventListener {


    private static final Logger LOGGER = Logger.getLogger(SpeechPreviewRecognizeEventListener.class);


    @Override
    public void onSpeechPhrase(RecognizeResponse response) {
        LOGGER.info("SpeechPreviewRecognizeEventListener onSpeechPhrase  : " + response.getBody());
    }

    @Override
    public void onSpeechHypothesis(RecognizeResponse response) {
        LOGGER.info("SpeechPreviewRecognizeEventListener onSpeechHypothesis : " + response.getBody());


    }

    @Override
    public void onSpeechStartDetected(RecognizeResponse response) {
    }

    @Override
    public void onTurnStart(RecognizeResponse response) {
        super.onTurnStart(response);
    }



}
