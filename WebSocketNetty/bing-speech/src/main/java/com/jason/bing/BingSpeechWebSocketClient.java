package com.jason.bing;

import com.jason.bing.util.MessageUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 必应语音识别webosket 接口实现
 * @Date: Created in 2018/5/20
 */
public class BingSpeechWebSocketClient {
    private static final String URL_FORMAT = "wss://speech.platform.bing.com/speech/recognition/#{recognitionMode}/cognitiveservices/v1?format=#{format}&language=#{language}&Ocp-Apim-Subscription-Key=#{subscriptionKey}&X-ConnectionId=#{connectionId}";

    private String url;
    private String connectionId;
    private String requestId;
    private RecognizerConfig recognizerConfig;
    private String subscriptionKey;

    private RecognizeWebSocket recognizeWebSocket ;




    public String getUrl() {
        this.connectionId = this.getUUID();
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

    public Session createRecognizer(RecognizerConfig recognizerConfig , String subscriptionKey , RecognizeEventListener recognizeEventListener) {
        if (recognizeEventListener == null) {
            throw new NullPointerException("recognizeEventListener required not null");
        }
        this.recognizerConfig = recognizerConfig;
        this.subscriptionKey = subscriptionKey;
        this.requestId = this.getUUID();

        SslContextFactory sslContextFactory = new SslContextFactory();
        WebSocketClient client = new WebSocketClient(sslContextFactory);
        recognizeWebSocket = new RecognizeWebSocket(recognizeEventListener);
        try
        {
            client.start();

            URI echoUri = new URI(this.getUrl());
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setHeader("Ocp-Apim-Subscription-Key" , this.getSubscriptionKey());
            Future<Session> fuq = client.connect(recognizeWebSocket,echoUri,request);
            return fuq.get();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            if (!client.isStopped()) {
//                client.stop();
            }
        }
        finally
        {

        }
        return null;
    }


    /*
     * 识别音频数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizer(byte[] audioData) {
        recognizeWebSocket.sendAudioData(audioData);
    }




    /*
     * 识别音频数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizer(String audioSource) {
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
                recognizeWebSocket.sendAudioData(MessageUtil.getAudioMessage(audioHeader , content));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            //audio 消息头
            byte[] audioHeader = MessageUtil.getAudioHeader(this.requestId);
            int bufLen = 8 * 1024;
            if (bufLen > data.length) {
                //文件数据小，直接一个包发出去
                recognizeWebSocket.sendAudioData(MessageUtil.getAudioMessage(audioHeader , data));
            } else {
                //数据分块上传识别
                byte[] buf = new byte[bufLen];
                int pos = 0;
                while (pos < data.length) {
                    int len = data.length - pos;
                    System.arraycopy(data , pos , buf , 0 , len < bufLen? len : bufLen);
                    pos += bufLen;
                    recognizeWebSocket.sendAudioData(MessageUtil.getAudioMessage(audioHeader , buf));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {
        recognizeWebSocket.close();
    }

//    createRecognizer(recognizerConfig, subscriptionKey);

//    createRecognizerWithFileAudioSource(recognizerConfig, subscriptionKey, files[0]);


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


    public String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
