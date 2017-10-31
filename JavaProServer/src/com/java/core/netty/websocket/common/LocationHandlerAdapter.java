package com.java.core.netty.websocket.common;

import com.alibaba.fastjson.JSON;
import com.java.core.netty.websocket.adapter.KeepAliveHandlerAdapter;
import com.java.core.netty.websocket.annotation.RequestMapping;
import com.java.core.netty.websocket.cache.WebSocketCacheManager;
import com.java.core.netty.websocket.cache.WebSocketClient;
import com.java.core.netty.websocket.resolver.AbstractControlFrameResolver;
import com.java.core.netty.websocket.resolver.DataFrameResolver;
import com.java.core.netty.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Collection;
import java.util.Map;

/**
 *
 * 定位数据处理器
 * Created by zhuangjiesen on 2017/9/13.
 */

@RequestMapping(uri = "/location.do")
public class LocationHandlerAdapter extends KeepAliveHandlerAdapter<TextWebSocketFrame> {



    @Override
    public void handlerWebSocketFrameData(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        System.out.println(" ---- LocationHandlerAdapter .....handlerWebSocketFrameData ....");


        String content = webSocketFrame.text();

        System.out.println("LocationHandlerAdapter ....content : " + content );


    }


    @Override
    public void handleResponse(Map<String, Object> params) {
        System.out.println(" ---- LocationHandlerAdapter .....handleResponse ....");

        String message = (String) params.get("message");
        WebSocketCacheManager wcm = applicationContext.getBean(WebSocketCacheManager.class);
        String uri = getUri();
        Collection<WebSocketClient> clients = wcm.getClientsByUri(uri);
        //批量发送数据
        MessageUtils.sendMessage(clients , message);


    }


    @Override
    public void onUpgradeCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {

    }

}
