package com.netease.ai.bing;

import com.netease.ai.bing.util.MessageUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 流式语音识别 websocket 连接
 * https://docs.microsoft.com/zh-cn/azure/cognitive-services/speech/concepts#transcription-responses
 * bing 文档
 * @Date: Created in 2018/5/20
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class RecognizeWebSocket {

    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;


    private RecognizeEventListener recognizeEventListener;
    private String requestId;

    public RecognizeWebSocket(RecognizeEventListener recognizeEventListener) {
        this.recognizeEventListener = recognizeEventListener;
        this.closeLatch = new CountDownLatch(1);
        this.requestId = MessageUtil.getUUID();
    }

    public RecognizeWebSocket()
    {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        return this.closeLatch.await(duration,unit);
    }


    public void close() {
        this.session.close();
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("Connection closed: %d - %s%n",statusCode,reason);
        this.session = null;
        this.closeLatch.countDown(); // trigger latch
    }

    @OnWebSocketError
    public void onError(Throwable throwable)
    {
        System.out.printf("throwable : %s ",throwable);
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.printf("Got connect: %s%n",session);
        this.session = session;
        try
        {
            if (this.recognizeEventListener != null) {
                RecognizeResponse response = new RecognizeResponse();
                response.setRequestId(this.requestId);
                response.setPath(SpeechEventConstant.BEFORE_CONNECTION_START);
                this.recognizeEventListener.onRecognizeEventTriggered(this, response);
            }

            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture(MessageUtil.getSpeechConfigMessage(this.requestId));
            fut.get();


        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        RecognizeResponse response = MessageUtil.getRecognizeResponse(msg);
        if (this.recognizeEventListener != null) {
            this.recognizeEventListener.onRecognizeEventTriggered(this, response);
        }
    }



    public void sendString(String message) {
        try {
            this.session.getRemote().sendString(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void sendAudioData(byte[] message) {
        try {
            this.session.getRemote().sendBytes(ByteBuffer.wrap(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







}
