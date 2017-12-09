package com.dragsun.websocket.resolver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

/**
 *
 * 处理控制帧接口
 *
 * Created by zhuangjiesen on 2017/9/13.
 */
public interface ControlFrameResolver {


    public void onWebSocketFrameClosed(ChannelHandlerContext ctx, CloseWebSocketFrame closeFrame, WebSocketServerHandshaker handshaker);

    public void onWebSocketFramePing(ChannelHandlerContext ctx, PingWebSocketFrame pingFrame);

    public void onWebSocketFramePong(ChannelHandlerContext ctx, PongWebSocketFrame pongFrame);


}
