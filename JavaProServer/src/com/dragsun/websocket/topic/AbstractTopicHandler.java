package com.dragsun.websocket.topic;

import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.server.WSMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhuangjiesen on 2018/1/25.
 */
public abstract class AbstractTopicHandler implements WSTopicHandler {


    @Override
    public void onSubscribe(ChannelHandlerContext ctx) {
    }

    @Override
    public void onUnSubscribe(ChannelHandlerContext ctx) {
    }

    @Override
    public void onMessageRecieved(ChannelHandlerContext ctx, WSMessage message) {
    }

    @Override
    public void onTopicRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
    }




}
