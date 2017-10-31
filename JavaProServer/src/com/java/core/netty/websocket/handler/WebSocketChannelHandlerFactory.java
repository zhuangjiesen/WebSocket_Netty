package com.java.core.netty.websocket.handler;

import com.java.core.netty.websocket.cache.WebSocketCacheManager;
import com.java.core.netty.websocket.mapping.RequestHandlerMapping;
import com.java.core.netty.websocket.resolver.UpgradeResolver;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.Constructor;

/**
 *
 *
 * websocket channelHandler 请求处理工厂类
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketChannelHandlerFactory  {

    private UpgradeResolver upgradeResolver;
    private RequestHandlerMapping requestHandlerMapping;
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

    public RequestHandlerMapping getRequestHandlerMapping() {
        return requestHandlerMapping;
    }

    public void setRequestHandlerMapping(RequestHandlerMapping requestHandlerMapping) {
        this.requestHandlerMapping = requestHandlerMapping;
    }

    public WebSocketCacheManager getWebSocketCacheManager() {
        return webSocketCacheManager;
    }

    public void setWebSocketCacheManager(WebSocketCacheManager webSocketCacheManager) {
        this.webSocketCacheManager = webSocketCacheManager;
    }
}
