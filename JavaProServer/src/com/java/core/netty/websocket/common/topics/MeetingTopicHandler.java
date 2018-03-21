package com.java.core.netty.websocket.common.topics;

import com.dragsun.websocket.annotation.WSTopic;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.server.WSMessage;
import com.dragsun.websocket.topic.AbstractTopicHandler;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhuangjiesen on 2018/1/25.
 */

@WSTopic(topic = "meeting")
public class MeetingTopicHandler extends AbstractTopicHandler {


    @Override
    public void onMessageRecieved(ChannelHandlerContext ctx, WSMessage message) {
        System.out.println(" index onMessageRecieved : " + message.getContent());

        MessageUtils.sendMessage(message.getTopic() , message.getContent());
    }

    @Override
    public void onTopicRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
        System.out.println(" index onTopicRegistyCompleted : -------------" );

    }
}
