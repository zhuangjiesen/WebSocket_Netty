package com.netease.ai.bing;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 常量配置记录
 * @Date: Created in 2018/5/20
 */
public interface SpeechEventConstant {
    public static final String SUBSCRIPTION_KEY = "xxxxxxx";
    public static final String LINE_SEP = "\r\n";
    public static final String MODE_CONVERSATION = "conversation";
    public static final String MODE_DICTATION = "dictation";

    public static final String BEFORE_CONNECTION_START = "before.connection.start";

    public static final String SPEECH_CONFIG = "speech.config";
    public static final String SPEECH_TELEMETRY = "speech.telemetry";
    public static final String SPEECH_FRAGMENT = "speech.fragment";
    public static final String SPEECH_PHRASE = "speech.phrase";
    public static final String TURN_START = "turn.start";
    public static final String SPEECH_STARTDETECTED = "speech.startDetected";
    public static final String SPEECH_ENDDETECTED = "speech.endDetected";
    public static final String TURN_END = "turn.end";


    public static final String X_REQUESTID = "X-RequestId";
    public static final String PATH = "Path";



}
