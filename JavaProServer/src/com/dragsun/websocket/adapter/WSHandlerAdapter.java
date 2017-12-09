package com.dragsun.websocket.adapter;

import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.resolver.DataFrameResolver;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public interface WSHandlerAdapter {

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
