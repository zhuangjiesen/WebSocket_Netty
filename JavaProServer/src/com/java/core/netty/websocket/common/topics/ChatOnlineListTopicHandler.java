package com.java.core.netty.websocket.common.topics;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.annotation.WSTopic;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.server.WSMessage;
import com.dragsun.websocket.topic.AbstractTopicHandler;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * 实现聊天在线人员的推送
 *
 * Created by zhuangjiesen on 2018/1/25.
 */

@WSTopic(topic = "chatOnlineList")
public class ChatOnlineListTopicHandler extends AbstractTopicHandler {


    private static ConcurrentHashMap<String , Object> onlineList = new ConcurrentHashMap<>();
    private static Object defaultObj = new Object();

    @Override
    public void onSubscribe(ChannelHandlerContext ctx) {
        super.onSubscribe(ctx);
        String id = ctx.channel().id().asLongText();
        if (!onlineList.contains(id)) {
            onlineList.put( id, defaultObj);
            pushOnlineList();
        }
        pushCurrentUser(ctx);
    }


    @Override
    public void onUnSubscribe(ChannelHandlerContext ctx) {
        super.onUnSubscribe(ctx);
        onlineList.remove(ctx.channel().id().asLongText());
    }


    public void pushCurrentUser(ChannelHandlerContext ctx) {
        String id = ctx.channel().id().asLongText();
        Map<String , Object> map = new HashMap();
        map.put("currentUser" ,id );
        MessageUtils.sendMessage("chatOnlineList" , JSONObject.toJSONString(map));
    }

    /*
    * 推送人员列表
    *
    * */
    public void pushOnlineList(){
        if (onlineList.size() > 0) {
            List<String > list = new ArrayList<>(onlineList.size());
            Set<String > keyset = onlineList.keySet();
            for (String key : keyset) {
                list.add(key);
            }
            Map<String , Object> map = new HashMap();
            map.put("chatOnlineList" ,list );
            MessageUtils.sendMessage("chatOnlineList" , JSONObject.toJSONString(map));
        }
    }


    @Override
    public void onMessageRecieved(ChannelHandlerContext ctx, WSMessage message) {
        System.out.println(" chatOnlineList onMessageRecieved : " + message.getContent());
    }

    @Override
    public void onTopicRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
        System.out.println(" chatOnlineList onTopicRegistyCompleted : -------------" );
    }




}
