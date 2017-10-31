package com.java.core.netty.websocket.handler.impl;

import com.java.core.netty.websocket.handler.WebSocketMessageHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.stereotype.Component;

/**
 * Created by zhuangjiesen on 2017/8/9.
 */

public class TextFrameWebSocketMessageHandler implements WebSocketMessageHandler {
    @Override
    public void onMessage(WebSocketFrame webSocketFrame) {
        TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) webSocketFrame;
        String message = textWebSocketFrame.text();

        System.out.println("textWebSocketFrame : " + ((TextWebSocketFrame) webSocketFrame).text());

    }
}
