package com.java.core.netty.websocket.adapter;

import com.java.core.netty.websocket.cache.WebSocketClient;
import com.java.core.netty.websocket.resolver.DataFrameResolver;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public interface HandlerAdapter {

    /*
    *
    * 处理客户端请求
    * */
    public void handleRequest(ChannelHandlerContext ctx, Object msg , WebSocketClient webSocketClient);
    /*
    *
    * 服务端处理(或者是推送处理)
    * */
    public void handleResponse(Map<String , Object> params);



    /*
    * 连接完成时调用
    * */
    public void onUpgradeCompleted(ChannelHandlerContext ctx,   WebSocketClient webSocketClient);


}
