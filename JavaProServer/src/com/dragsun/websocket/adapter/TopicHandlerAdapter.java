package com.dragsun.websocket.adapter;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.server.WSMessage;
import com.dragsun.websocket.topic.WSTopicHandler;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 用来处理 topic   的默认处理器
 * Created by zhuangjiesen on 2017/9/13.
 */
public class TopicHandlerAdapter extends KeepAliveHandlerAdapter<TextWebSocketFrame> {

    public ConcurrentHashMap<String , ConcurrentHashMap<String , WebSocketClient>> topicClientsMap = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String , WSTopicHandler> topicsMap = new ConcurrentHashMap<>();




    /*
    *
    *  topic 与 WSTopicHandler 绑定
    * */
    public void subscribeHandler(String topic ,WSTopicHandler topicHandler ){
        topicsMap.put(topic , topicHandler);
    }



    /*
    * 客户端绑定 topic
    * */
    public void subscribeClient(String topic ,WebSocketClient client ){
        if (client == null) return ;


        if (client.getTopics() == null) {
            synchronized (topicClientsMap) {
                //double check
                if (client.getTopics() == null) {
                    client.setTopics(new HashSet<>());
                }
            }
        }
        synchronized (topicClientsMap) {
            client.getTopics().add(topic);
        }

        ConcurrentHashMap<String , WebSocketClient > clients = topicClientsMap.get(topic);
        if (clients == null) {
            synchronized (topicClientsMap) {
                if (clients == null) {
                    clients = new ConcurrentHashMap<String , WebSocketClient >();
                    topicClientsMap.put(topic , clients);
                }
            }
        }
        clients.put(client.getChannelHandlerContext().channel().id().asLongText() , client);
    }


    public WSTopicHandler getHandler(String topic) {
        WSTopicHandler topicHandler = null;
        topicHandler = topicsMap.get(topic);
        return topicHandler;
    }



    /*
    * 发送消息
    * */
    public void sendMessage(String topic , String message) {
        if (message == null || message.length() == 0) {
            return ;
        }
        if (topic.startsWith("/")) {
            topic = topic.substring(1);
        }
        ConcurrentHashMap<String , WebSocketClient > clients = topicClientsMap.get(topic);
        if (clients != null && clients.size() > 0) {
            WSMessage wsMessage = new WSMessage();
            wsMessage.setTopic(topic);
            wsMessage.setContent(message);
            String sendMsg = MessageUtils.swapMessage(wsMessage);
            Collection<WebSocketClient> clientsList = null;
            clientsList = clients.values();
            for (WebSocketClient socketClient : clientsList) {
                String channelId = socketClient.getChannelHandlerContext().channel().id().asLongText();
                //判断是否下线,清除相应的客户端
                if (socketClient.isClosed()) {
                    clients.remove(channelId);
                    onUnSubscribe(topic , socketClient.getChannelHandlerContext());
                } else {
                    MessageUtils.sendMessage(socketClient , sendMsg);
                }
            }
        }
    }




    @Override
    public void handleResponse(Map<String , Object> params) {
    }



    @Override
    public void onUpgradeCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
        Set<String> topics = webSocketClient.getTopics();
        if (topics != null && topics.size() > 0) {
            for (String topic : topics) {
                topic = topic.trim();
                ConcurrentHashMap<String , WebSocketClient > clients = topicClientsMap.get(topic);
                if (clients == null) {
                    synchronized (topicClientsMap) {
                        if (clients == null) {
                            clients = new ConcurrentHashMap<String , WebSocketClient >();
                            topicClientsMap.put(topic , clients);
                        }
                    }
                }
                String channelId = ctx.channel().id().asLongText();
                clients.put(channelId , webSocketClient);
                // 触发事件
                WSTopicHandler handler = topicsMap.get(topic);
                if (handler != null) {
                    handler.onTopicRegistyCompleted(ctx , webSocketClient);
                }
            }
        }
    }


    /*
    * 获取前端发送的消息
    * */
    @Override
    public void handlerWebSocketFrameData(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        String text = webSocketFrame.text();
        if (text != null && text.length() > 0) {
            WSMessage message = MessageUtils.parseText(text);
            WSTopicHandler topicHandler = topicsMap.get(message.getTopic());
            if (topicHandler != null) {
                //订阅消息
                if ("subscribe".equals(message.getContentType())) {
                    WebSocketCacheManager cacheManager = applicationContext.getBean(WebSocketCacheManager.class);
                    String id = ctx.channel().id().asLongText();
                    this.subscribeClient(message.getTopic() , cacheManager.getWebSocketClient(id));

                    onSubscribe(message.getTopic() , ctx);
                } else if ("unsubscribe".equals(message.getContentType())) {
                    //取消订阅
                    onUnSubscribe(message.getTopic() , ctx);
                } else {
                    topicHandler.onMessageRecieved(ctx , message);
                }
            }
        }
    }



    /*
    *
    * 清除绑定的协议的客户端
    *
    *
    * */
    @Override
    protected void doOnWebSocketFrameClosed(ChannelHandlerContext ctx, CloseWebSocketFrame closeFrame, WebSocketServerHandshaker handshaker) {
        super.doOnWebSocketFrameClosed(ctx, closeFrame, handshaker);

        WebSocketCacheManager cacheManager = applicationContext.getBean(WebSocketCacheManager.class);
        String id = ctx.channel().id().asLongText();
        WebSocketClient webSocketClient = cacheManager.getWebSocketClient(id);
        Set<String> topics = webSocketClient.getTopics();
        if (topics != null) {
            for (String topic : topics) {
                ConcurrentHashMap<String , WebSocketClient > clients = topicClientsMap.get(topic);
                if (clients != null) {
                    clients.remove(id);
                    onUnSubscribe(topic , ctx);
                }
            }
        }
    }





    /*
    * 客户端断开时，触发退订事件
    *
    * */
    public void onUnSubscribe(String topic ,ChannelHandlerContext ctx ) {
        WSTopicHandler handler = topicsMap.get(topic);
        if (handler != null) {
            handler.onUnSubscribe(ctx);
        }
    }


    public void onSubscribe(String topic ,ChannelHandlerContext ctx ) {
        WSTopicHandler handler = topicsMap.get(topic);
        if (handler != null) {
            handler.onSubscribe(ctx);
        }
    }

}
