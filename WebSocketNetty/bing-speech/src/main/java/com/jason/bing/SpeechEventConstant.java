package com.jason.bing;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 常量配置记录
 * 官网文档 https://docs.microsoft.com/zh-cn/azure/cognitive-services/speech/concepts
 * @Date: Created in 2018/5/20
 */
public interface SpeechEventConstant {

    public static final String BING_SN_PREFIX = "bing_sn_prefix:%s";


    /** bing 语音识别开关 **/
    public static final boolean BING_ENABLE = true;


    /** 每个音频文件时间  **/
    public static final long PER_AUDIO_TIME = 5 * 60 * 1000;

    /**  每个连接的时间限制 **/
    public static final int LIMIT_MILISECONDS = 5 * 60 * 1000;

    public static final String SUBSCRIPTION_KEY = "c9f881d1f5064db29c50588d5718946f";
    /** 换行符 **/
    public static final String LINE_SEP = "\r\n";
    public static final String MODE_CONVERSATION = "conversation";
    public static final String MODE_DICTATION = "dictation";
    /** 连接建立前 触发事件**/
    public static final String BEFORE_CONNECTION_START = "before.connection.start";


    /** close 事件 **/
    public static final String SPEECH_CLOSE = "speech.close";
    /** error 事件 **/
    public static final String SPEECH_ERROR = "speech.error";

    public static final String SPEECH_CONFIG = "speech.config";
    public static final String SPEECH_TELEMETRY = "speech.telemetry";
    public static final String SPEECH_FRAGMENT = "speech.fragment";
    public static final String SPEECH_PHRASE = "speech.phrase";
    public static final String TURN_START = "turn.start";
    public static final String SPEECH_STARTDETECTED = "speech.startDetected";
    public static final String SPEECH_ENDDETECTED = "speech.endDetected";
    /** 识别到文件结尾 或者调用manager.close() 即识别结束 **/
    public static final String SPEECH_FILE_END = "speech.file.end";

    public static final String TURN_END = "turn.end";


    public static final String X_REQUESTID = "X-RequestId";
    public static final String PATH = "Path";



}
