package com.dragsun.websocket.cache;

import com.dragsun.websocket.utils.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketClientDao {

    //心跳  已发送次数
    public static ConcurrentHashMap<String , Integer> pingPongChannelsMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , WebSocketClient > clientsMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String , ConcurrentHashMap<String , WebSocketClient >> uriClientsMap = new ConcurrentHashMap<>();


    private static ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);

    static {
//        scheduleService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//            }
//        } , 1L , 10 , TimeUnit.SECONDS);
    }


    private Lock pingPongLock = new ReentrantLock();


    public ConcurrentHashMap<String , WebSocketClient > getClientsMapByUri (String uri) {
        return uriClientsMap.get(uri);
    }




    public Collection<WebSocketClient> getClientsByUri (String uri) {
        ConcurrentHashMap<String , WebSocketClient > clientsMap = null;
        clientsMap = uriClientsMap.get(uri);
        if (clientsMap != null) {
            return clientsMap.values();
        }
        return null;
    }

    public void putClientByUri (String uri , String channelId , WebSocketClient webSocketClient) {
        ConcurrentHashMap<String , WebSocketClient > clients = null;
        if ((clients = uriClientsMap.get(uri)) == null) {
            // double check
            synchronized (uriClientsMap) {
                if ((clients = uriClientsMap.get(uri)) == null) {
                    clients = new ConcurrentHashMap<>();
                    uriClientsMap.put(uri , clients);
                }
            }
        }
        clients.put(channelId , webSocketClient);
    }


    public void removeClientByUriChannelId (String uri , String channelId ) {
        ConcurrentHashMap<String , WebSocketClient > clients = null;
        if ((clients = uriClientsMap.get(uri)) != null) {
            clients.remove(channelId );
        }
    }





    public void putWebSocketClient(String channelId , WebSocketClient webSocketClient ) {
        if (clientsMap.containsKey(channelId)) {
            return ;
        }
        try {
            putClientByUri(webSocketClient.getUri() , channelId , webSocketClient);
            clientsMap.put(channelId , webSocketClient);
            pingPongChannelsMap.remove(channelId);
        } catch (Exception e) {
            LogUtils.logError(this, e );
        }
    }

    public WebSocketClient getWebSocketClient(String channelId ) {
        WebSocketClient webSocketClient = clientsMap.get(channelId);
        return webSocketClient;
    }


    public ChannelHandlerContext getChannelHandlerContext(String channelId ) {
        WebSocketClient webSocketClient = clientsMap.get(channelId);
        if (webSocketClient != null && webSocketClient.getChannelHandlerContext() != null) {
            return webSocketClient.getChannelHandlerContext();
        }
        return null;
    }


    public WebSocketClient removeChannelHandlerContext(String channelId ) {
        WebSocketClient webSocketClient = clientsMap.remove(channelId);
        if (webSocketClient != null) {
            String uri = webSocketClient.getUri();
            removeClientByUriChannelId(uri , channelId);
            pingPongChannelsMap.remove(channelId);
        }
        return webSocketClient;
    }





    public List<ChannelHandlerContext> getChannelHandlerContextList() {
        if (clientsMap.isEmpty()) {
            return null;
        }
        Set<String> keySet = clientsMap.keySet();
        List<ChannelHandlerContext> list = new LinkedList<>();
        for (String key : keySet) {
            WebSocketClient webSocketClient = clientsMap.get(key);
            list.add(webSocketClient.getChannelHandlerContext());
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }



    /*
    * 全部发送心跳
    *
    * */
    public void sendPingMessageToAll(){
        pingPongChannelsMap.clear();
        if (clientsMap.isEmpty()) {
            return ;
        }

        Set<String> keySet = clientsMap.keySet();
        for (String key : keySet) {
            WebSocketClient webSocketClient = clientsMap.get(key);
            //往客户端发ping 客户端会返回pong 可以用来判断客户端存活
            PingWebSocketFrame pingWebSocketFrame = new PingWebSocketFrame();
            webSocketClient.getChannelHandlerContext().channel().writeAndFlush(pingWebSocketFrame);
            //标记为已发送
            pingPongChannelsMap.put(key , 1 );
        }
    }



    public void getPongMessage(String channelId) {
        if (channelId == null) {
            return ;
        }
        pingPongChannelsMap.remove(channelId);
    }


    public void clearNotPingPongMessage() {
        if (clientsMap.isEmpty()) {
            return ;
        }
        CloseWebSocketFrame closeWebSocketFrame = new CloseWebSocketFrame();
        Set<String> keySet = pingPongChannelsMap.keySet();
        for (String key : keySet) {
            Integer status = pingPongChannelsMap.get(key);
            if (status != null && status.intValue() == 1) {
                WebSocketClient webSocketClient = clientsMap.get(key);
                //关闭websocket // 握手关闭连接
                webSocketClient.getHandshaker().close(webSocketClient.getChannelHandlerContext().channel() , closeWebSocketFrame);
            }
            pingPongChannelsMap.remove(key);
        }
        pingPongChannelsMap.clear();
    }




    /*
    * 全部发送消息
    * 往客户端推送消息
    *
    * */
    public void sendMessageToAll(String message){
        if (clientsMap.isEmpty()) {
            return ;
        }
        Set<String> keySet = clientsMap.keySet();
        for (String key : keySet) {
            WebSocketClient webSocketClient = clientsMap.get(key);
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(message);
            if (webSocketClient.getChannelHandlerContext().channel().isOpen() && webSocketClient.getChannelHandlerContext().channel().isWritable()) {
                webSocketClient.getChannelHandlerContext().channel().writeAndFlush(textWebSocketFrame);
            } else {
                clientsMap.remove(key);
            }
        }
    }




}
