package com.java.core.netty.websocket.chat;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.adapter.KeepAliveHandlerAdapter;
import com.dragsun.websocket.annotation.WSRequestMapping;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.constant.WebSocketConstant;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Collection;
import java.util.Map;

/**
 * 聊天处理器
 * Created by zhuangjiesen on 2017/9/13.
 */

@WSRequestMapping(uri = "/chat" )
public class ChatHandlerAdapter   extends KeepAliveHandlerAdapter<TextWebSocketFrame> {


    @Override
    public void handlerWebSocketFrameData(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        System.out.println(" ---- ChatHandlerAdapter .....handlerWebSocketFrameData ....");

        String id = ctx.channel().id().asLongText();
        String content = webSocketFrame.text();
        if (!WebSocketConstant.PING_MESSAGE.equals(content)) {
            System.out.println("ChatHandlerAdapter ....content : " + content );
            JSONObject chatContent = JSONObject.parseObject(content);
            String contentText = chatContent.getString("content");
            String targetId = chatContent.getString("targetId");

            WebSocketCacheManager webSocketCacheManager = applicationContext.getBean(WebSocketCacheManager.class);
            WebSocketClient webSocketClient = webSocketCacheManager.getWebSocketClient(targetId);

            JSONObject sendContent = new JSONObject();
            sendContent.put("content" , contentText);
            sendContent.put("type" , 1);
            sendContent.put("sendId" , ctx.channel().id().asLongText());
            MessageUtils.sendMessage(webSocketClient , sendContent.toJSONString() );
        }

        System.out.println("ChatHandlerAdapter ....content : " + content );

    }


    @Override
    public void handleResponse(Map<String, Object> params) {
        System.out.println(" ---- ChatHandlerAdapter .....handleResponse ....");

        WebSocketCacheManager webSocketCacheManager = applicationContext.getBean(WebSocketCacheManager.class);
        //聊天通道
        Collection<WebSocketClient> clients = webSocketCacheManager.getClientsByUri(getUri());
        if (clients != null) {
            for (WebSocketClient client : clients) {
                Channel channel = client.getChannelHandlerContext().channel();
                String id = channel.id().asLongText();
                JSONObject json = new JSONObject();
                json.put("id" , id);
                json.put("type" , 0);
                TextWebSocketFrame textFrame = new TextWebSocketFrame(json.toJSONString());
                channel.writeAndFlush(textFrame);
            }
        }

    }



    @Override
    public void onUpgradeCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {

        String id = ctx.channel().id().asLongText();
        JSONObject json = new JSONObject();
        json.put("id" , id);
        json.put("type" , 0);
        TextWebSocketFrame textFrame = new TextWebSocketFrame(json.toJSONString());
        ctx.writeAndFlush(textFrame);

    }

}
