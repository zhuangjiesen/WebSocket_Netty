package com.jason.bing;

import com.alibaba.fastjson.JSONObject;
import com.jason.bing.util.BingUtil;
import com.jason.bing.util.MessageUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: bing speech api 事件的处理
 * 官方文档 ：https://docs.microsoft.com/zh-cn/azure/cognitive-services/speech/api-reference-rest/websocketprotocol#message-types
 * @Date: Created in 2018/5/21
 */
public abstract class AbstractRecognizeEventListener implements RecognizeEventListener {

    //停顿 300ms * 10000 等于 100纳秒单位
    public static final int breakMSecond = 200 * 10000;
    //单词数
    public static final int wordSizeLimit = 0;
    //缓存时效性
    public static final int CACHE_LIVETIME_SECONDS = 12 * 60 * 60;


    private static final Logger LOGGER = Logger.getLogger(AbstractRecognizeEventListener.class);

    //封装 telemetry 消息
    public List<Map<String, String>> metrics = new ArrayList<>(3);
    //封装 telemetry 消息
    public Map<String, List<String>> receivedMessages = new HashMap<>();
    public Map<String, String> metricConnection = new HashMap<>(4);
//    private Map<String, String> metricListeningTrigger = new HashMap<>(4);

    public void onSpeechPhrase (RecognizeResponse response){}
    @Deprecated
    public void onSpeechFragment(RecognizeResponse response){}

    public void onSpeechHypothesis(RecognizeResponse response){}
    public void onSpeechEndDetected(RecognizeResponse response){}
    public void onSpeechStartDetected(RecognizeResponse response){}
    public void onSpeechClosed(RecognizeResponse response){}
    public void onSpeechError(RecognizeResponse response){}

    public void onTurnEnd(RecognizeResponse response){}
    public void onTurnStart(RecognizeResponse response){
        metricConnection.put("End" , BingUtil.getTimestamp() );
        metrics.add(metricConnection);
        metricConnection.clear();
    }


    /*
     *
     * 文件解析，触发文件结果解析
     * @author zhuangjiesen
     * @date 2018/5/25 下午4:42
     * @param
     * @return
     */
    public void onFileEnd(RecognizeResponse response){

    }


    /*
     *
     * websocket 连接前调用
     * @author zhuangjiesen
     * @date 2018/5/21 下午3:51
     * @param
     * @return
     */
    public void beforeConnectionStart(RecognizeResponse response){
        metricConnection.put("name" , "Connection" );
        metricConnection.put("id" , response.getRequestId() );
        metricConnection.put("start" , BingUtil.getTimestamp() );
    }


    /*
     *
     * telemetry 文档规定把每个消息的返回时间戳回传给服务端，需要记录数据
     * @author zhuangjiesen
     * @date 2018/5/21 下午4:38
     * @param
     * @return
     */
    private void addReceivedMessages(String path) {
        List<String> messageList = null;
        if ((messageList = receivedMessages.get(path)) == null) {
            messageList = new ArrayList<>();
            receivedMessages.put(path , messageList);
        }
        messageList.add(BingUtil.getTimestamp());
    }


    private String getTelemetryMessage(RecognizeResponse response) {

        StringBuilder messageSb = new StringBuilder(MessageUtil.getSpeechTelemetryHeader(response.getRequestId()));

        JSONObject telemetryBody = new JSONObject();
        telemetryBody.put("Metrics" , metrics);
        telemetryBody.put("ReceivedMessages" , receivedMessages);

        messageSb.append(telemetryBody.toJSONString());

        //help gc
        metrics.clear();
        receivedMessages.clear();
        return messageSb.toString();
    }

    /*
     * 事件转换
     * @author zhuangjiesen
     * @date 2018/5/21 下午2:34
     * @param
     * @return
     */
    @Override
    public void onRecognizeEventTriggered(Object recognizationClient,  RecognizeResponse response) {

        String path = response.getPath();
//        addReceivedMessages(path);
//        LOGGER.debug(String.format(" path : %s , requestid : %s , text : %s " , path  , response.getRequestId() , response.getBody()));
//        LOGGER.info(String.format(" path : %s , requestid : %s , text : %s " , path  , response.getRequestId() , response.getBody()));
        switch (path) {
            case SpeechEventConstant.SPEECH_FRAGMENT:
                onSpeechFragment(response);
                break;
            case SpeechEventConstant.SPEECH_PHRASE:
                onSpeechPhrase(response);
                break;
            case SpeechEventConstant.SPEECH_HYPOTHESIS:
                onSpeechHypothesis(response);
                break;
            case SpeechEventConstant.TURN_START:
                onTurnStart(response);
                break;
            case SpeechEventConstant.TURN_END:
                //关闭websocket 连接
//                recognizeWebSocket.sendString(getTelemetryMessage(response));
                onTurnEnd(response);
                if (recognizationClient instanceof SpeechRecognizationClient) {
                    SpeechRecognizationClient speechRecognizationClient = (SpeechRecognizationClient)recognizationClient;
                    speechRecognizationClient.close();
                    if (speechRecognizationClient.isLast()) {
                        onFileEnd(response);
                    }
                } else if (recognizationClient instanceof SyncRecognizationClient) {
                    SyncRecognizationClient client = (SyncRecognizationClient)recognizationClient;
                    client.close();
                }
                break;
            case SpeechEventConstant.SPEECH_STARTDETECTED:
                onSpeechStartDetected(response);
                break;
            case SpeechEventConstant.SPEECH_ENDDETECTED:
                onSpeechEndDetected(response);
                break;
            case SpeechEventConstant.BEFORE_CONNECTION_START:
                beforeConnectionStart(response);
                break;
            case SpeechEventConstant.SPEECH_FILE_END:
                onFileEnd(response);
                break;
            case SpeechEventConstant.SPEECH_CLOSE:
                onSpeechClosed(response);
                break;
            case SpeechEventConstant.SPEECH_ERROR:
                onSpeechError(response);
                break;
        }


    }
}
