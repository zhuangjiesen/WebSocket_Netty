package com.java.core.netty.websocket.common.topics;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.annotation.WSTopic;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.server.WSMessage;
import com.dragsun.websocket.topic.AbstractTopicHandler;
import com.dragsun.websocket.utils.ApplicationContextHolder;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * 实现聊天功能
 *
 * Created by zhuangjiesen on 2018/1/25.
 */

@WSTopic(topic = "chat")
public class ChatTopicHandler extends AbstractTopicHandler {


    @Override
    public void onMessageRecieved(ChannelHandlerContext ctx, WSMessage message) {
        System.out.println(" chat onMessageRecieved : " + message.getContent());

        JSONObject jsonObject = JSONObject.parseObject(message.getContent());
        String content = jsonObject.getString("content");
        String targetId = jsonObject.getString("targetId");
        String sendUserId = jsonObject.getString("sendUserId");

        //发送消息
        WebSocketCacheManager webSocketCacheManager = ApplicationContextHolder.applicationContext.getBean(WebSocketCacheManager.class);
        WebSocketClient client = webSocketCacheManager.getWebSocketClient(targetId);
        if (client != null) {
            JSONObject msg = new JSONObject();
            msg.put("content" , content );
            msg.put("sendUserId" , sendUserId );
            MessageUtils.sendMessage(client , msg.toJSONString());
        }

    }

    @Override
    public void onTopicRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
        System.out.println(" chat onTopicRegistyCompleted : -------------" );
    }

    @Override
    public void onSubscribe(ChannelHandlerContext ctx) {
        super.onSubscribe(ctx);
    }




}
