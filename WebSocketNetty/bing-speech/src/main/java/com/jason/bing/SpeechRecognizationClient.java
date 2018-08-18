package com.jason.bing;


import com.jason.bing.util.MessageUtil;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.concurrent.Future;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/25
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SpeechRecognizationClient extends BingRecognizationClient {

    private static final Logger LOGGER = Logger.getLogger(SpeechRecognizationClient.class);


    public SpeechRecognizationClient(RecognizerConfig recognizerConfig, String subscriptionKey, String urlFormat, RecognizeEventListener recognizeEventListener) {
        super(recognizerConfig , subscriptionKey, urlFormat, recognizeEventListener);
    }


    public SpeechRecognizationClient(RecognizerConfig recognizerConfig, String subscriptionKey, RecognizeEventListener recognizeEventListener) {
        super(recognizerConfig , subscriptionKey , recognizeEventListener);
    }



    public SpeechRecognizationClient(RecognizeEventListener recognizeEventListener) {
        super(recognizeEventListener);
    }



    /*============== websocket 连接状态 ====================*/



    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        eventTrigger(SpeechEventConstant.SPEECH_CLOSE);
        LOGGER.info(String.format("bing WebSocket Connection close statusCode %d ,reason : %s " , statusCode , reason));
    }

    @OnWebSocketError
    public void onError(Throwable throwable)
    {
        eventTrigger(SpeechEventConstant.SPEECH_ERROR);
        LOGGER.error(throwable.getMessage() , throwable );
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        LOGGER.info(" bing recognization connection connected !.");
        this.webSocketSession = session;
        try {
            eventTrigger(SpeechEventConstant.BEFORE_CONNECTION_START);

            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture(MessageUtil.getSpeechConfigMessage(this.requestId));
            fut.get();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage() , t);
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        RecognizeResponse response = MessageUtil.getRecognizeResponse(msg);
        if (this.recognizeEventListener != null) {
            this.recognizeEventListener.onRecognizeEventTriggered(this , response);
        }
    }






}
