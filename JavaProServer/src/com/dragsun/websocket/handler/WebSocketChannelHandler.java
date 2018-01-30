package com.dragsun.websocket.handler;

import com.dragsun.websocket.utils.LogUtils;
import com.dragsun.websocket.adapter.WSHandlerAdapter;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.mapping.WSRequestHandlerMapping;
import com.dragsun.websocket.resolver.UpgradeResolver;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketChannelHandler  extends ChannelInboundHandlerAdapter {


    private UpgradeResolver upgradeResolver;

    private WSRequestHandlerMapping requestHandlerMapping;

    private WebSocketCacheManager webSocketCacheManager;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // TODO Auto-generated method stub
        if (msg instanceof FullHttpRequest) {
            //处理http请求
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            if (uri.startsWith("//")) {
                request.setUri(uri.substring(1));
            }



            WebSocketClient webSocketClient = new WebSocketClient();
            String id = getChannelCtxId(ctx);
            //先注册请求处理器
            try {
                requestHandlerMapping.registHandlerAdapter(request ,  webSocketClient);
            } catch (Exception e) {
                LogUtils.logError(this , e);
                //处理异常 没有具体映射的请求处理器
                upgradeResolver.handleRequestError(ctx, request , e);
                return ;
            }
            WebSocketServerHandshaker handshaker = null;
            // upgrade 与 websocket 握手过程
            if ((handshaker = upgradeResolver.handleRequest(ctx, request )) != null) {
                //设置uri
                webSocketClient.setUri(MessageUtils.getHttpGetUri(request.uri()));
                //设置请求参数
                webSocketClient.setReqParam(MessageUtils.getHttpGetParams(request.uri()));
                webSocketClient.setChannelHandlerContext(ctx);
                webSocketClient.setHandshaker(handshaker);
                //注册连接管理器
                webSocketCacheManager.putWebSocketClient(id , webSocketClient);
                //完成后调用
                webSocketClient.getHandlerAdapter().onUpgradeCompleted(ctx , webSocketClient);
            }
        } else if (msg instanceof WebSocketFrame) {
            //处理websocket请求
            String id = getChannelCtxId(ctx);
            //获取请求处理器
            WSHandlerAdapter handlerAdapter = null;
            if ((handlerAdapter = requestHandlerMapping.getFrameHandlerAdapterById(id)) != null) {
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
        LogUtils.logError(this, cause );
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
