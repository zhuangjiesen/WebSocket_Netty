package com.java.core.netty.websocket.chat;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.adapter.KeepAliveHandlerAdapter;
import com.dragsun.websocket.annotation.WSRequestMapping;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 在线人员推送
 * Created by zhuangjiesen on 2017/9/13.
 */

@WSRequestMapping(uri = "/chatOnlineList")
public class ChatOnlineListHandlerAdapter extends KeepAliveHandlerAdapter<TextWebSocketFrame> {


    @Override
    public void handlerWebSocketFrameData(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        System.out.println(" ---- ChatHandlerAdapter .....handlerWebSocketFrameData ....");


        String content = webSocketFrame.text();

        System.out.println("ChatHandlerAdapter ....content : " + content );

    }


    @Override
    public void handleResponse(Map<String, Object> params) {
        System.out.println(" ---- ChatHandlerAdapter .....handleResponse ....");


        WebSocketCacheManager webSocketCacheManager = applicationContext.getBean(WebSocketCacheManager.class);
        //聊天通道
        Collection<WebSocketClient> clients = webSocketCacheManager.getClientsByUri("/chat.do");

        //推送通道
        Collection<WebSocketClient> subscribeClients = webSocketCacheManager.getClientsByUri(getUri());
        if (clients != null) {

            Map<String , Object > onLineList = new HashMap();
            for (WebSocketClient client : clients) {
                String id = client.getChannelHandlerContext().channel().id().asLongText();
                onLineList.put(id , 1 );
            }
            for (WebSocketClient client : subscribeClients) {
                String id = client.getChannelHandlerContext().channel().id().asLongText();
                JSONObject message = new JSONObject(onLineList);

                JSONObject newMessage = (JSONObject) message.clone();
                MessageUtils.sendMessage(client , newMessage.toJSONString());
            }
        }






    }




    @Override
    public void onUpgradeCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {

    }


}
