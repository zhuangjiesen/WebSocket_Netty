package com.java.core.netty.websocket.utils;

import com.java.core.netty.websocket.cache.WebSocketClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;

/**
 * Created by zhuangjiesen on 2017/9/14.
 */
public class MessageUtils {


    public static void sendMessage(Collection<WebSocketClient> clients , String message){
        if (clients != null) {
            for (WebSocketClient client : clients) {
                ChannelHandlerContext channelHandlerContext = client.getChannelHandlerContext();
                TextWebSocketFrame textFrame = new TextWebSocketFrame(message);
                channelHandlerContext.writeAndFlush(textFrame);
            }
        }
    }


    public static void sendMessage(WebSocketClient client , String message){
        ChannelHandlerContext channelHandlerContext = client.getChannelHandlerContext();
        TextWebSocketFrame textFrame = new TextWebSocketFrame(message);
        channelHandlerContext.writeAndFlush(textFrame);
    }


    public static void sendPingMessage(WebSocketClient client){
        ChannelHandlerContext channelHandlerContext = client.getChannelHandlerContext();
        if (channelHandlerContext.channel().isWritable()) {
            PingWebSocketFrame ping = new PingWebSocketFrame();
            channelHandlerContext.writeAndFlush(ping);
        }
    }


}
