package com.dragsun.websocket.handler.websocket;


import com.dragsun.websocket.client.CloseStatus;
import com.dragsun.websocket.client.WebSocketSession;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: websocket事件触发器
 * @Date: Created in 2018/5/12
 */
public interface WebSocketHandler {

    void beforeConnectionUpgraded(WebSocketSession webSocketSession) throws Exception;

    void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception;

    void handleMessage(WebSocketSession webSocketSession,WebSocketFrame webSocketFrame) throws Exception;

    void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception;

    void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception;

    boolean supportsPartialMessages();

}
