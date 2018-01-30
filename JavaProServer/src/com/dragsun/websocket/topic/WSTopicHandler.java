package com.dragsun.websocket.topic;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.server.WSMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 *
 * websocket topic 处理器
 * Created by zhuangjiesen on 2017/11/16.
 */
public interface WSTopicHandler {

    public void onSubscribe(ChannelHandlerContext ctx );

    public void onUnSubscribe( ChannelHandlerContext ctx );

    /*
    * 接收到客户端消息
    *
    * */
    public void onMessageRecieved(ChannelHandlerContext ctx, WSMessage message);

    /*
    * 连接建立
    *
    * */
    public void onTopicRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient);

}
