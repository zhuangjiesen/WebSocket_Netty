package com.dragsun.websocket.protocol;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.cache.WebSocketClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 *
 * websocket 子协议处理器
 * Created by zhuangjiesen on 2017/11/16.
 */
public interface WSProtocolHandler {

    /*
    * 封装推送消息
    *
    * */
    public String wrapperPushedMessage(Map<String , Object> params);

    /*
    * 接收到客户端消息
    *
    * */
    public void onMessageRecieved(ChannelHandlerContext ctx , JSONObject message);

    /*
    * 连接建立
    *
    * */
    public void onProtocolRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient);

}
