package com.java.core.netty.websocket.cache;

import com.java.core.netty.websocket.adapter.HandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 *
 * websocket 客户端POJO 类
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketClient {

    private WebSocketServerHandshaker handshaker ;
    private ChannelHandlerContext channelHandlerContext ;
    private HandlerAdapter handlerAdapter;
    private String uri;


    public WebSocketClient(WebSocketServerHandshaker handshaker, ChannelHandlerContext channelHandlerContext, HandlerAdapter handlerAdapter, String uri) {
        this.handshaker = handshaker;
        this.channelHandlerContext = channelHandlerContext;
        this.handlerAdapter = handlerAdapter;
        this.uri = uri;
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


    public HandlerAdapter getHandlerAdapter() {
        return handlerAdapter;
    }

    public void setHandlerAdapter(HandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }
}
