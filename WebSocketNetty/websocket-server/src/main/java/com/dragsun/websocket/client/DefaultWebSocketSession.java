package com.dragsun.websocket.client;

import com.dragsun.websocket.handler.websocket.WebSocketHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
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
public class DefaultWebSocketSession implements WebSocketSession {

    private String id;
    private URI uri;
    private HttpHeaders handshakeHeaders;
    private Map<String, List<String>> parameters;

    private Map<String, Object> attributes;
    private Principal principal;
    private InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;
    private String acceptedProtocol;
    private int textMessageSizeLimit;
    private int binaryMessageSizeLimit;
    private List<WebSocketExtension> extensions;
    private ChannelHandlerContext channelHandlerContext;
    private WebSocketHandler webSocketHandler;
    private WebSocketServerHandshaker webSocketServerHandshaker;


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public URI getUri() {
        return this.uri;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return this.handshakeHeaders;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Principal getPrincipal() {
        return this.principal;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public String getAcceptedProtocol() {
        return this.acceptedProtocol;
    }

    @Override
    public void setTextMessageSizeLimit(int var1) {

    }

    @Override
    public int getTextMessageSizeLimit() {
        return this.textMessageSizeLimit;
    }

    @Override
    public void setBinaryMessageSizeLimit(int var1) {

    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return this.binaryMessageSizeLimit;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return this.extensions;
    }

    @Override
    public void sendMessage(WebSocketFrame webSocketFrame)  {
        this.channelHandlerContext.writeAndFlush(webSocketFrame);
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close()  {
        if (this.webSocketServerHandshaker != null && this.channelHandlerContext != null && this.channelHandlerContext.channel().isOpen()) {
            CloseWebSocketFrame closeWebSocketFrame = new CloseWebSocketFrame(CloseStatus.NORMAL.getCode() , CloseStatus.NORMAL.getReason());
            this.webSocketServerHandshaker.close(this.channelHandlerContext.channel() , closeWebSocketFrame );
        }
    }

    @Override
    public void close(CloseStatus var1)  {
        if (this.webSocketServerHandshaker != null && this.channelHandlerContext != null && this.channelHandlerContext.channel().isOpen()) {
            CloseWebSocketFrame closeWebSocketFrame = new CloseWebSocketFrame(var1.getCode() , var1.getReason());
            this.webSocketServerHandshaker.close(this.channelHandlerContext.channel() , closeWebSocketFrame );
        }
    }

    @Override
    public ChannelHandlerContext getChannelHandlerContext() {
        return this.channelHandlerContext;
    }

    @Override
    public WebSocketHandler getWebSocketHandler() {
        return this.webSocketHandler;
    }

    @Override
    public WebSocketServerHandshaker getWebSocketServerHandshaker() {
        return this.webSocketServerHandshaker;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public void setHandshakeHeaders(HttpHeaders handshakeHeaders) {
        this.handshakeHeaders = handshakeHeaders;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void setAcceptedProtocol(String acceptedProtocol) {
        this.acceptedProtocol = acceptedProtocol;
    }

    public void setExtensions(List<WebSocketExtension> extensions) {
        this.extensions = extensions;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    public void setWebSocketServerHandshaker(WebSocketServerHandshaker webSocketServerHandshaker) {
        this.webSocketServerHandshaker = webSocketServerHandshaker;
    }

    @Override
    public Map<String, List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }
}
