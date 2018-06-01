package com.jason.bing;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: bing 配置类
 * @Date: Created in 2018/5/20
 */
public class RecognizerConfig {

    private String recognitionMode;
    private String language;
    private String format;

    public String getRecognitionMode() {
        return recognitionMode;
    }

    public void setRecognitionMode(String recognitionMode) {
        this.recognitionMode = recognitionMode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public static RecognizerConfig getDefaultRecognizerConfig(){
        return RecognizerConfigHolder.defaultRecognizerConfig;
    }


    public RecognizerConfig(String recognitionMode, String language, String format) {
        this.recognitionMode = recognitionMode;
        this.language = language;
        this.format = format;
    }


    public RecognizerConfig() {
    }

    //内部类单例 初始化默认参数
    private static class RecognizerConfigHolder {
        /** 默认参数 **/
        public static RecognizerConfig defaultRecognizerConfig = new RecognizerConfig("Dictation" ,"en-US" , "Simple" );
    }

}
