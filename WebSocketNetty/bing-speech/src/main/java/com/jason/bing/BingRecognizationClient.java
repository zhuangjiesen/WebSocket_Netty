package com.jason.bing;

import com.jason.bing.util.BingUtil;
import com.jason.bing.util.MessageUtil;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/31
 */
public class BingRecognizationClient {

    private static final Logger LOGGER = Logger.getLogger(BingRecognizationClient.class);

    /** 刷新websocket 缓存区的计数器 **/
    private static final int FLUSH_TIMES = 3;

    private AtomicInteger flushCountor = new AtomicInteger(FLUSH_TIMES);

    private boolean isFlush() {
        if (flushCountor.decrementAndGet() <= 0) {
            flushCountor.set(FLUSH_TIMES);
            return true;
        }
        return false;
    }


    public static final String URL_FORMAT = "wss://speech.platform.bing.com/speech/recognition/#{recognitionMode}/cognitiveservices/v1?format=#{format}&language=#{language}&Ocp-Apim-Subscription-Key=#{subscriptionKey}&X-ConnectionId=#{connectionId}";

    protected String url;
    protected String connectionId;
    protected String requestId;
    protected RecognizerConfig recognizerConfig;
    protected String subscriptionKey;
    /** 不是最后一个连接**/
    private boolean last;


    protected RecognizeEventListener recognizeEventListener;
    protected Session webSocketSession;


    /** 转异步的用法 **/
    private CountDownLatch sycnLatch;


    public String getUrl() {
        this.connectionId = BingUtil.getUUID();
        String recognitionMode = recognizerConfig.getRecognitionMode();
        String format = recognizerConfig.getFormat();
        String language = recognizerConfig.getLanguage();
        String mUrl = URL_FORMAT.replace("#{recognitionMode}" , recognitionMode);
        mUrl = mUrl.replace("#{format}" , format);
        mUrl = mUrl.replace("#{language}" , language);
        mUrl = mUrl.replace("#{subscriptionKey}" , this.subscriptionKey);
        mUrl = mUrl.replace("#{connectionId}" , this.connectionId);
        this.url = mUrl;
        return this.url;
    }


    public BingRecognizationClient(RecognizerConfig recognizerConfig, String subscriptionKey, RecognizeEventListener recognizeEventListener) {
        this.recognizerConfig = recognizerConfig;
        this.subscriptionKey = subscriptionKey;
        this.recognizeEventListener = recognizeEventListener;
    }



    public BingRecognizationClient(RecognizeEventListener recognizeEventListener) {
        this.recognizerConfig = RecognizerConfig.getDefaultRecognizerConfig();
        this.subscriptionKey = SpeechEventConstant.SUBSCRIPTION_KEY ;
        this.recognizeEventListener = recognizeEventListener;
    }



    public void createConnection () {
        LOGGER.debug("bing WebSocket createConnection.! ");
        if (this.webSocketSession != null) {
            return ;
        }

        if (recognizeEventListener == null) {
            throw new NullPointerException("recognizeEventListener required not null");
        }
        this.recognizerConfig = recognizerConfig;
        this.subscriptionKey = subscriptionKey;
        this.requestId = BingUtil.getUUID();
        this.recognizeEventListener = recognizeEventListener;
        SslContextFactory sslContextFactory = new SslContextFactory();
        WebSocketClient client = new WebSocketClient(sslContextFactory);
        try
        {
            client.start();

            URI echoUri = new URI(this.getUrl());
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setHeader("Ocp-Apim-Subscription-Key" , this.getSubscriptionKey());
            Future<Session> fuq = client.connect(this ,echoUri,request);
            this.webSocketSession = fuq.get();
        }
        catch (Throwable t)
        {
            LOGGER.error(t.getMessage() , t);
            if (!client.isStopped()) {
                try {
                    client.stop();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage() , e);
                }
            }
        }
    }


    /*
     * 识别音频数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizer(byte[] audioData) {
        boolean isFlush = isFlush();
        createConnection();
        try {
            byte[] audioHeader = MessageUtil.getAudioHeader(this.requestId);
//            this.webSocketSession.getRemote().sendPartialBytes(ByteBuffer.wrap(MessageUtil.getAudioMessage(audioHeader , audioData)) , isFlush );
            this.webSocketSession.getRemote().sendBytes(ByteBuffer.wrap(MessageUtil.getAudioMessage(audioHeader , audioData)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /*
     * 识别音频数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizer(String audioSource) {
        createConnection();
        try {
            //audio 消息头
            byte[] audioHeader = MessageUtil.getAudioHeader(this.requestId);
            String filePath = audioSource;
            File file = new File(filePath);
            FileInputStream fins = new FileInputStream(file);
            byte[] buf = new byte[8 * 1024];
            int len = -1;
            while ((len = fins.read(buf)) != -1) {
                byte[] content = null;
                if (len == buf.length) {
                    content = buf;
                } else {
                    content = new byte[len];
                    System.arraycopy(buf , 0 , content , 0 , len);
                }

                this.webSocketSession.getRemote().sendBytes(ByteBuffer.wrap(MessageUtil.getAudioMessage(audioHeader , content)));

            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage() , e);
        }
    }



    /*
     * 识别音频数据 byte[] 文件数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizerAudioData(byte[] data) {
        createConnection();
        try {
            //audio 消息头
            byte[] audioHeader = MessageUtil.getAudioHeader(this.requestId);
            int bufLen = 8 * 1024;
            if (bufLen > data.length) {
                //文件数据小，直接一个包发出去
                this.webSocketSession.getRemote().sendBytes(ByteBuffer.wrap(MessageUtil.getAudioMessage(audioHeader , data)));
            } else {
                //数据分块上传识别
                byte[] buf = new byte[bufLen];
                int pos = 0;
                while (pos < data.length) {
                    int len = data.length - pos;
                    System.arraycopy(data , pos , buf , 0 , len < bufLen? len : bufLen);
                    pos += bufLen;
                    this.webSocketSession.getRemote().sendBytes(ByteBuffer.wrap(MessageUtil.getAudioMessage(audioHeader , buf)));
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage() , e);
        }
    }


    public void close() {
        webSocketSession.close();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public RecognizerConfig getRecognizerConfig() {
        return recognizerConfig;
    }

    public void setRecognizerConfig(RecognizerConfig recognizerConfig) {
        this.recognizerConfig = recognizerConfig;
    }

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public void setSubscriptionKey(String subscriptionKey) {
        this.subscriptionKey = subscriptionKey;
    }



    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }




    /*
     *
     * 触发对应的事件
     * @author zhuangjiesen
     * @date 2018/6/1 上午12:00
     * @param
     * @return
     */
    public void eventTrigger(String event) {
        if (this.recognizeEventListener != null) {
            RecognizeResponse response = new RecognizeResponse();
            response.setRequestId(this.requestId);
            response.setPath(event);
            this.recognizeEventListener.onRecognizeEventTriggered(this , response);
        }
    }



}
