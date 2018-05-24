package com.dragsun.websocket.client;

import com.dragsun.websocket.handler.websocket.WebSocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public interface WebSocketSession {

    Map<String, List<String>> getParameters();

    String getId();

    URI getUri();

    HttpHeaders getHandshakeHeaders();

    Map<String, Object> getAttributes();

    Principal getPrincipal();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    String getAcceptedProtocol();

    void setTextMessageSizeLimit(int textMessageSizeLimit);

    int getTextMessageSizeLimit();

    void setBinaryMessageSizeLimit(int binaryMessageSizeLimit);

    int getBinaryMessageSizeLimit();

    List<WebSocketExtension> getExtensions();

    void sendMessage(WebSocketFrame webSocketFrame) ;

    boolean isOpen();

    void close();

    void close(CloseStatus closeStatus);


    ChannelHandlerContext getChannelHandlerContext();

    WebSocketHandler getWebSocketHandler();

    WebSocketServerHandshaker getWebSocketServerHandshaker();


}
