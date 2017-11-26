package com.java.core.netty.websocket.handler;

import com.java.core.netty.websocket.cache.WebSocketCacheManager;
import com.java.core.netty.websocket.mapping.WSRequestHandlerMapping;
import com.java.core.netty.websocket.resolver.UpgradeResolver;

/**
 *
 *
 * websocket channelHandler 请求处理工厂类
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketChannelHandlerFactory  {

    private UpgradeResolver upgradeResolver;
    private WSRequestHandlerMapping requestHandlerMapping;
    private WebSocketCacheManager webSocketCacheManager;

    public WebSocketChannelHandler newWebSocketChannelHandler() {
        WebSocketChannelHandler webSocketChannelHandler = new WebSocketChannelHandler();
        webSocketChannelHandler.setRequestHandlerMapping(requestHandlerMapping);
        webSocketChannelHandler.setUpgradeResolver(upgradeResolver);
        webSocketChannelHandler.setWebSocketCacheManager(webSocketCacheManager);
        return webSocketChannelHandler;
    }



    public WebSocketOutboundChannelHandler newWebSocketOutboundChannelHandler() {
        WebSocketOutboundChannelHandler webSocketOutboundChannelHandler = new WebSocketOutboundChannelHandler();
        webSocketOutboundChannelHandler.setWebSocketCacheManager(webSocketCacheManager);
        return webSocketOutboundChannelHandler;
    }




    public UpgradeResolver getUpgradeResolver() {
        return upgradeResolver;
    }

    public void setUpgradeResolver(UpgradeResolver upgradeResolver) {
        this.upgradeResolver = upgradeResolver;
    }

    public WSRequestHandlerMapping getRequestHandlerMapping() {
        return requestHandlerMapping;
    }

    public void setRequestHandlerMapping(WSRequestHandlerMapping requestHandlerMapping) {
        this.requestHandlerMapping = requestHandlerMapping;
    }

    public WebSocketCacheManager getWebSocketCacheManager() {
        return webSocketCacheManager;
    }

    public void setWebSocketCacheManager(WebSocketCacheManager webSocketCacheManager) {
        this.webSocketCacheManager = webSocketCacheManager;
    }
}
