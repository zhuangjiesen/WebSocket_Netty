package com.dragsun.websocket.handler;

import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketOutboundChannelHandler extends ChannelOutboundHandlerAdapter {

    private WebSocketCacheManager webSocketCacheManager;

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        String id = ctx.channel().id().asLongText();
        WebSocketClient webSocketClient = webSocketCacheManager.getWebSocketClient(id);
        if (webSocketClient != null) {
            WebSocketServerHandshaker handshaker = webSocketClient.getHandshaker();
            Channel channel = ctx.channel();
            if (channel.isOpen()) {
                handshaker.close(ctx.channel() , new CloseWebSocketFrame());
            }
            webSocketCacheManager.removeWebSocketClient(id);
        }
        super.close(ctx, promise);
    }


    public WebSocketCacheManager getWebSocketCacheManager() {
        return webSocketCacheManager;
    }

    public void setWebSocketCacheManager(WebSocketCacheManager webSocketCacheManager) {
        this.webSocketCacheManager = webSocketCacheManager;
    }
}
