package com.java.core.netty.websocket.resolver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public abstract class AbstractControlFrameResolver implements ControlFrameResolver {


    public void onWebSocketFrameClosed(ChannelHandlerContext ctx , CloseWebSocketFrame closeFrame , WebSocketServerHandshaker handshaker){
        doOnWebSocketFrameClosed(ctx , closeFrame , handshaker);
        if (handshaker != null){
            handshaker.close(ctx.channel(), closeFrame.retain());
        }
    }

    public void onWebSocketFramePing(ChannelHandlerContext ctx , PingWebSocketFrame pingFrame){
        //ping 消息一定要回复 pong
        try {
            doOnWebSocketFramePing(ctx , pingFrame);
        } finally {
            ctx.channel().write(
                    new PongWebSocketFrame(pingFrame.content().retain()));
        }
    }

    public void onWebSocketFramePong(ChannelHandlerContext ctx , PongWebSocketFrame pongFrame ){
        //获取pong
        doOnWebSocketFramePong(ctx ,pongFrame );
    }



    /*
    * 继承以下方法重写
    *
    * */
    protected void doOnWebSocketFrameClosed(ChannelHandlerContext ctx , CloseWebSocketFrame closeFrame , WebSocketServerHandshaker handshaker){
    }

    protected void doOnWebSocketFramePing(ChannelHandlerContext ctx , PingWebSocketFrame pingFrame){
    }

    protected void doOnWebSocketFramePong(ChannelHandlerContext ctx , PongWebSocketFrame pongFrame ){
    }



}
