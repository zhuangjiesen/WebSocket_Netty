package com.jason.bing;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 连接管理器，处理必应 websocket 10min连接断开的问题
 * 用在前端进行实施音频流的音频识别的接口
 * @Date: Created in 2018/5/25
 */
public class SpeechRecognizationClientManager {

    private static final Logger LOGGER = Logger.getLogger(SpeechRecognizationClientManager.class);


    private String url;
    private volatile SpeechRecognizationClient recognizationClient;

    private RecognizerConfig recognizerConfig;
    private String subscriptionKey;
    private RecognizeEventListener recognizeEventListener;
    private long endTime = 0;


    public SpeechRecognizationClientManager(RecognizerConfig recognizerConfig, String subscriptionKey, RecognizeEventListener recognizeEventListener) {
        this.recognizerConfig = recognizerConfig;
        this.subscriptionKey = subscriptionKey;
        this.recognizeEventListener = recognizeEventListener;
    }


    public SpeechRecognizationClientManager(String subscriptionKey, RecognizeEventListener recognizeEventListener) {
        this.recognizerConfig = RecognizerConfig.getDefaultRecognizerConfig();
        this.subscriptionKey = subscriptionKey;
        this.recognizeEventListener = recognizeEventListener;
    }


    public SpeechRecognizationClientManager(RecognizeEventListener recognizeEventListener) {
        this.recognizerConfig = RecognizerConfig.getDefaultRecognizerConfig();
        this.subscriptionKey = SpeechEventConstant.PREVIEW_SUBSCRIPTION_KEY;
        this.recognizeEventListener = recognizeEventListener;
    }




    /*
     *
     * 判断连接是否超时
     * @author zhuangjiesen
     * @date 2018/5/25 下午3:30
     * @param
     * @return
     */
    public boolean isConnectionOutTime () {
        long now = System.currentTimeMillis();
        if (this.endTime == 0) {
            this.endTime = now + SpeechEventConstant.LIMIT_MILISECONDS;
            LOGGER.info(" connection isConnectionOutTime true ");
            return true;
        } else if (now > this.endTime) {
            this.endTime = now + SpeechEventConstant.LIMIT_MILISECONDS;
            return true;
        }
        return false;
    }


    public SpeechRecognizationClient getRecognizationClient() {
        if (isConnectionOutTime() || this.recognizationClient == null) {
            this.recognizationClient = buildNewRecognizationClient();
        }
        return recognizationClient;
    }

    public SpeechRecognizationClient buildNewRecognizationClient() {
        LOGGER.info(" buildNewRecognizationClient  ");
//        SpeechRecognizationClient client = new SpeechRecognizationClient(this.recognizerConfig , this.subscriptionKey , this.recognizeEventListener);
        SpeechRecognizationClient client = new SpeechRecognizationClient(this.recognizerConfig , this.subscriptionKey ,SpeechEventConstant.PREVIEW_URL_FORMAT , this.recognizeEventListener);
        return client;
    }


    public void close() {
        LOGGER.info(" Connection close ! ");
        if (this.recognizationClient != null) {
            this.recognizationClient.close();
        }

        //主动关闭，表示连接不用再进行
        RecognizeResponse response = new RecognizeResponse();
        response.setRequestId(this.recognizationClient.getRequestId());
        response.setPath(SpeechEventConstant.SPEECH_FILE_END);
        this.recognizeEventListener.onRecognizeEventTriggered(this.recognizationClient , response);
    }

    /*
     * 识别音频数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizer(byte[] audioData) {
        this.getRecognizationClient().recognizer(audioData);
    }




    /*
     * 识别音频文件数据
     * @author zhuangjiesen
     * @date 2018/5/20 下午3:53
     * @param
     * @return
     */
    public void recognizer(String audioSource) {
        try {
            SpeechRecognizationClient client = null;
            String filePath = audioSource;
            File file = new File(filePath);
            FileInputStream fins = new FileInputStream(file);
            byte[] buf = new byte[8 * 1024];
            int len = -1;
            boolean start = true;
            while ((len = fins.read(buf)) != -1) {
                byte[] content = null;
                if (len == buf.length) {
                    content = buf;
                } else {
                    content = new byte[len];
                    System.arraycopy(buf , 0 , content , 0 , len);
                }
                if (start) {
                    start = false;
                    //去掉wav头
                    byte[] data = new byte[content.length - 44];
                    System.arraycopy(content , 44 , data , 0 , data.length);
                    content = data;
                }
                //audio 消息头
                client = this.getRecognizationClient();
                client.recognizerAudioData(content);
            }
            if (client != null) {
                client.setLast(true);
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
    public void recognizerAudioData(byte[] audioData) {
        try {
            //去掉wav头
            byte[] data = audioData;
//            byte[] data = new byte[audioData.length - 44];
//            System.arraycopy(audioData , 44 , data , 0 , data.length);

            SpeechRecognizationClient client = null;
            int bufLen =  16 * 1024;
            if (bufLen > data.length) {
                //文件数据小，直接一个包发出去
                client = this.getRecognizationClient();
                //audio 消息头
                client.recognizer(data);
            } else {
                //数据分块上传识别
                byte[] buf = new byte[bufLen];
                int pos = 44;
                while (pos < data.length) {
                    int len = data.length - pos;
                    System.arraycopy(data , pos , buf , 0 , len < bufLen? len : bufLen);
                    pos += bufLen;

                    client = this.getRecognizationClient();
                    client.recognizer(buf);
                }
            }
            if (client != null) {
                client.setLast(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
