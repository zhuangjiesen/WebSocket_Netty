package com.jason.bing;

import com.alibaba.fastjson.JSONObject;
import com.jason.bing.util.MessageUtil;
import com.jason.bing.util.BingUtil;

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
    //封装 telemetry 消息
    public List<Map<String, String>> metrics = new ArrayList<>(3);
    //封装 telemetry 消息
    public Map<String, List<String>> receivedMessages = new HashMap<>();
    public Map<String, String> metricConnection = new HashMap<>(4);
//    private Map<String, String> metricListeningTrigger = new HashMap<>(4);

    public void onSpeechPhrase (RecognizeResponse response){}
    public void onSpeechFragment(RecognizeResponse response){}
    public void onSpeechEndDetected(RecognizeResponse response){}
    public void onSpeechStartDetected(RecognizeResponse response){}

    public void onTurnEnd(RecognizeResponse response){}
    public void onTurnStart(RecognizeResponse response){
        metricConnection.put("End" , BingUtil.getTimestamp() );
        metrics.add(metricConnection);
        metricConnection = null;
    }

    /*
     *
     * websocket连接前调用
     * @author zhuangjiesen
     * @date 2018/5/21 下午3:51
     * @param
     * @return
     */
    public void beforeConnectionStart(RecognizeResponse response){
        System.out.println("========beforeConnectionStart========");
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
        receivedMessages = null;
        metrics = null;
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
    public void onRecognizeEventTriggered(RecognizeWebSocket recognizeWebSocket, RecognizeResponse response) {
        String path = response.getPath();
//        addReceivedMessages(path);
        switch (path) {
            case SpeechEventConstant.SPEECH_FRAGMENT:
                onSpeechFragment(response);
                break;
            case SpeechEventConstant.SPEECH_PHRASE:
                onSpeechPhrase(response);
                break;
            case SpeechEventConstant.TURN_START:
                onTurnStart(response);
                break;
            case SpeechEventConstant.TURN_END:
//                recognizeWebSocket.sendString(getTelemetryMessage(response));
                onTurnEnd(response);
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
        }


    }
}
