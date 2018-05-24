package com.dragsun.websocket.handler.websocket;

import com.dragsun.websocket.client.CloseStatus;
import com.dragsun.websocket.client.WebSocketSession;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/15
 */
public abstract class AbstractWebSocketHandler implements WebSocketHandler {


    @Override
    public void beforeConnectionUpgraded(WebSocketSession webSocketSession) throws Exception {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketFrame webSocketFrame) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
