package com.jason.bing;


import com.jason.bing.util.MessageUtil;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 同步处理的 bing websocket连接， 调用await() 后，只会在close() 或者 error() 触发时，返回
 * @Date: Created in 2018/5/25
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SyncRecognizationClient extends BingRecognizationClient {

    private static final Logger LOGGER = Logger.getLogger(SyncRecognizationClient.class);


    /** 转异步的用法 **/
    private CountDownLatch sycnLatch;


    public SyncRecognizationClient(RecognizeEventListener recognizeEventListener) {
        super(recognizeEventListener);
        sycnLatch = new CountDownLatch(1);
    }


    public void await(){
        try {
            sycnLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage() , e);
        }
    }

    /*============== websocket 连接状态 ====================*/


    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        LOGGER.info(String.format("bing WebSocket Connection close statusCode %d ,reason : %s " , statusCode , reason));
        eventTrigger(SpeechEventConstant.SPEECH_CLOSE);
        if (this.sycnLatch != null && this.sycnLatch.getCount() > 0) {
            this.sycnLatch.countDown();
        }
    }

    @OnWebSocketError
    public void onError(Throwable throwable)
    {
        LOGGER.error(throwable.getMessage() , throwable );
        eventTrigger(SpeechEventConstant.SPEECH_ERROR);
        if (this.sycnLatch != null && this.sycnLatch.getCount() > 0) {
            this.sycnLatch.countDown();
        }
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
