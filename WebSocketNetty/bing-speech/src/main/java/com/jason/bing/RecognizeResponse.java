package com.jason.bing;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/20
 */
public class RecognizeResponse {

    private Map<String , String> headers = new HashMap<>();
    private String body;
    private String path;
    private String requestId;
    private JSONObject bodyEntity;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeaders(String name , String value) {
        if (this.headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name , value);
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String name) {
        if (this.headers != null) {
            return this.headers.get(name);
        }
        return null;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        if (this.path == null && this.headers != null) {
            return path = this.headers.get(SpeechEventConstant.PATH);
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JSONObject getBodyEntity() {
        return bodyEntity;
    }

    public void setBodyEntity(JSONObject bodyEntity) {
        this.bodyEntity = bodyEntity;
    }


    public Object getBodyValue(String name) {
        if (this.bodyEntity != null) {
            return this.bodyEntity.get(name);
        }
        return null;
    }

    public String getRequestId() {
        if (this.headers != null) {
            return requestId = this.headers.get(SpeechEventConstant.X_REQUESTID);
        }
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}


