package com.java.core.netty.websocket.resolver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public interface DataFrameResolver<T extends WebSocketFrame> {


    /*
    * 处理数据帧接口
    *
    * */
    public void handlerWebSocketFrameData(ChannelHandlerContext ctx, T webSocketFrame);


}
