package com.dragsun.websocket.cache;

import com.dragsun.websocket.adapter.WSHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.Map;
import java.util.Set;

/**
 *
 * websocket 客户端POJO 类
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketClient {

    private WebSocketServerHandshaker handshaker ;
    private ChannelHandlerContext channelHandlerContext ;
    /** 请求处理器 **/
    private WSHandlerAdapter handlerAdapter;
    /*是否有protocol */
    private boolean hasSubProtocols;
    private boolean isClosed;
    private String uri;
    private String[] protocols;
    private Map<String ,Object> reqParam;


    public Map<String, Object> getReqParam() {
        return reqParam;
    }

    public void setReqParam(Map<String, Object> reqParam) {
        this.reqParam = reqParam;
    }

    public WebSocketServerHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketServerHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public WSHandlerAdapter getHandlerAdapter() {
        return handlerAdapter;
    }

    public void setHandlerAdapter(WSHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }

    public boolean isHasSubProtocols() {
        return hasSubProtocols;
    }

    public void setHasSubProtocols(boolean hasSubProtocols) {
        this.hasSubProtocols = hasSubProtocols;
    }

    public String[] getProtocols() {
        return protocols;
    }

    public void setProtocols(String[] protocols) {
        this.protocols = protocols;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
