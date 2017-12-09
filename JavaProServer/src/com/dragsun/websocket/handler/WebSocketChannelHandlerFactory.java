package com.dragsun.websocket.handler;


import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.mapping.WSRequestHandlerMapping;
import com.dragsun.websocket.resolver.UpgradeResolver;

/**
*
*
* websocket channelHandler 请求处理工厂类
* 生成 WebSocketNettyServer 中的netty 的ChannelHandler 的处理类
* Created by zhuangjiesen on 2017/9/13.
*/
public class WebSocketChannelHandlerFactory  {

    private UpgradeResolver upgradeResolver;
    private WSRequestHandlerMapping requestHandlerMapping;
    private WebSocketCacheManager webSocketCacheManager;

    public WebSocketChannelHandler newWebSocketChannelHandler() {
        WebSocketChannelHandler webSocketChannelHandler = new WebSocketChannelHandler();
        // 设置请求uri 与请求处理器的 映射处理器
        webSocketChannelHandler.setRequestHandlerMapping(requestHandlerMapping);
        // websocket 升级请求处理器
        webSocketChannelHandler.setUpgradeResolver(upgradeResolver);
        // webscoket 客户端存储器
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
