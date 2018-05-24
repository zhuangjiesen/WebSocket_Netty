package com.dragsun.websocket.handler.websocket;

import com.dragsun.websocket.client.CloseStatus;
import com.dragsun.websocket.client.WebSocketSession;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public class SimpleWebSocketHandler implements WebSocketHandler {

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
