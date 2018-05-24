package com.jason.bing;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 识别事件监听
 * @Date: Created in 2018/5/20
 */
public interface RecognizeEventListener {

    public void onRecognizeEventTriggered(RecognizeWebSocket recognizeWebSocket, RecognizeResponse response);

}
