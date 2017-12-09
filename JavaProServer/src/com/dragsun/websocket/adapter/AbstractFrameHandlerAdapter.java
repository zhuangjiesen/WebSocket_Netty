package com.dragsun.websocket.adapter;

import com.alibaba.fastjson.JSON;
import com.dragsun.websocket.annotation.WSRequestMapping;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.resolver.AbstractControlFrameResolver;
import com.dragsun.websocket.resolver.DataFrameResolver;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;

/**
 *
 * 父类处理器
 * 封装了 control frame 的默认处理逻辑若需要拓展可以自己重写方法
 * 提供了 applicationContext 用于获取spring bean 逻辑处理业务
 *
 * 也可以通过重写 doHandleRequest() 来添加逻辑
 *
 * Created by zhuangjiesen on 2017/9/13.
 */

public abstract class AbstractFrameHandlerAdapter<T extends WebSocketFrame> extends AbstractControlFrameResolver
        implements WSHandlerAdapter, DataFrameResolver<T>, ApplicationContextAware {

    public ApplicationContext applicationContext;


    public void handleRequest(ChannelHandlerContext ctx, Object msg , WebSocketClient webSocketClient){
        if (webSocketClient != null && msg instanceof WebSocketFrame ) {
            WebSocketFrame frame = (WebSocketFrame) msg;

            doHandleRequest(ctx , frame , webSocketClient );

            // 判断是否关闭链路的指令
            if ((frame instanceof CloseWebSocketFrame)) {
                onWebSocketFrameClosed(ctx, (CloseWebSocketFrame)frame , webSocketClient.getHandshaker());
                return ;
            } else if (frame instanceof PingWebSocketFrame) {
                // 判断是否ping消息
                onWebSocketFramePing(ctx, (PingWebSocketFrame)frame);
                return;
            } else if (frame instanceof PongWebSocketFrame) {
                onWebSocketFramePong(ctx, (PongWebSocketFrame)frame);
                return;
            } else  if (!(frame instanceof TextWebSocketFrame)) {
                throw new UnsupportedOperationException(String.format(
                        "%s frame types not supported", frame.getClass().getName()));
            } else {
                //TextWebSocketFrame 消息处理
                // 本例程仅支持文本消息，不支持二进制消息
                handlerWebSocketFrameData(ctx , (T) frame);
            }
        }


    }




    /*
    * 后置请求处理器
    *
    *
    * */
    public void doHandleRequest(ChannelHandlerContext ctx, Object msg , WebSocketClient webSocketClient) {

    }



    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }


    public String getUri(){
        String uri = null;
        WSRequestMapping requestMapping = this.getClass().getAnnotation(WSRequestMapping.class);
        if (requestMapping != null) {
            uri = requestMapping.uri();
        }
        return uri;
    }




}
