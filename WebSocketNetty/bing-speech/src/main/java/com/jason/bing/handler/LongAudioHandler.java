package com.jason.bing.handler;

import com.jason.bing.*;
import com.jason.bing.util.WordUtil;
import org.apache.log4j.Logger;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 长语音文件识别监听者
 * 坑： 因为每个音频文件都截取成 SpeechEventConstant.PER_AUDIO_TIME 大小的文件，一个一个进行识别，
 *     结果是：每个文件的时间轴都会从0 开始，所以从 turnEnd事件时，保存最后一个时间戳
 * @Date: Created in 2018/5/31
 */
public class LongAudioHandler extends BingAudioFileHandler {

    private static final Logger LOGGER = Logger.getLogger(LongAudioHandler.class);

    private long lastStartTime;



    @Override
    public void onSpeechPhrase(RecognizeResponse response) {
        WordInfo wordInfo = WordUtil.parsePhrase(response.getBodyEntity() , lastStartTime);
//        LOGGER.info("--- onSpeechPhrase --- : " + JSONObject.toJSONString(wordInfo));
        //计算时间轴
        doOnSpeechPhrase(wordInfo);
    }



    @Override
    public void onSpeechClosed(RecognizeResponse response) {
        lastStartTime += (SpeechEventConstant.PER_AUDIO_TIME / 1000);
    }


}
