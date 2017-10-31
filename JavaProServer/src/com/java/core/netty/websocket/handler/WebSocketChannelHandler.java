package com.java.core.netty.websocket.handler;

import com.java.core.netty.websocket.adapter.HandlerAdapter;
import com.java.core.netty.websocket.cache.WebSocketCacheManager;
import com.java.core.netty.websocket.cache.WebSocketClient;
import com.java.core.netty.websocket.mapping.RequestHandlerMapping;
import com.java.core.netty.websocket.resolver.UpgradeResolver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketChannelHandler  extends ChannelInboundHandlerAdapter {


    private UpgradeResolver upgradeResolver;

    private RequestHandlerMapping requestHandlerMapping;

    private WebSocketCacheManager webSocketCacheManager;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO Auto-generated method stub
        if (msg instanceof FullHttpRequest) {
            //处理http请求
            FullHttpRequest request = (FullHttpRequest) msg;

            WebSocketClient webSocketClient = null;
            String id = getChannelCtxId(ctx);
            //先注册请求处理器
            HandlerAdapter handlerAdapter = null;
            try {
                handlerAdapter = requestHandlerMapping.registHandlerAdapter(request.uri() , id);
            } catch (Exception e) {
                e.printStackTrace();
                //处理异常 没有具体映射的请求处理器
                upgradeResolver.handleRequestError(ctx, request , e);
                return ;
            }
            if ((webSocketClient = upgradeResolver.handleRequest(ctx, request , handlerAdapter)) != null) {
                //注册连接管理器
                webSocketCacheManager.putWebSocketClient(id , webSocketClient);
                //完成后调用
                handlerAdapter.onUpgradeCompleted(ctx , webSocketClient);
            }
        } else if (msg instanceof WebSocketFrame) {
            //处理websocket请求
            HandlerAdapter handlerAdapter = null;
            String id = getChannelCtxId(ctx);
            //获取请求处理器
            if ((handlerAdapter = (HandlerAdapter) requestHandlerMapping.getFrameHandlerAdapterById(id)) != null) {
                WebSocketFrame webSocketFrame = (WebSocketFrame) msg;
                WebSocketClient webSocketClient = webSocketCacheManager.getWebSocketClient(id);
                //处理请求
                handlerAdapter.handleRequest(ctx , webSocketFrame , webSocketClient );
            }
        } else {
            throw new RuntimeException("无法处理的请求");
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("WebSocketServerHandler exceptionCaught ..."  + cause.getMessage() );
        cause.printStackTrace();
    }


    public String getChannelCtxId(ChannelHandlerContext ctx){
        String id = ctx.channel().id().asLongText();
        return id;
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
