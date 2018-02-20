package com.dragsun.websocket.server;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhuangjiesen on 2018/1/25.
 */
public class WSMessage {

    private static AtomicLong _id = new AtomicLong(0);
    private Map<String , String> header;
    private String topic ;
    private String contentType;
    private String content;
    private String tag;

    private Long id;


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

    public void newId(){
        id = _id.incrementAndGet();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
