package com.dragsun.websocket.server;

import java.util.Map;

/**
 * Created by zhuangjiesen on 2018/1/25.
 */
public class WSMessage {

    private Map<String , String> header;
    private String topic ;
    private String contentType;
    private String content;
    private String tag;


    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
