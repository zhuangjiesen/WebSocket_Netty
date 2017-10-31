package com.java.core.netty.websocket.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WebSocketCacheManager {


    private WebSocketClientDao webSocketClientDao;


    public void putWebSocketClient(String id , WebSocketClient webSocketClient){
        webSocketClientDao.putWebSocketClient(id , webSocketClient);
    }



    public Collection<WebSocketClient> getClientsByUri (String uri) {
       return webSocketClientDao.getClientsByUri(uri);
    }


    public WebSocketClient getWebSocketClient(String id ){
        WebSocketClient webSocketClient = null;
        webSocketClient = webSocketClientDao.getWebSocketClient(id);
        return webSocketClient;
    }


    public Collection<WebSocketClient> getAllClients () {
        return WebSocketClientDao.clientsMap.values();
    }

    /*
    * 已经发送ping 消息
    *
    * */
    public void putPingClient (String channelId) {
        Integer pingTimes = WebSocketClientDao.pingPongChannelsMap.get(channelId);
        if (pingTimes != null) {
            WebSocketClientDao.pingPongChannelsMap.put(channelId , pingTimes.intValue() + 1);
        } else {
            WebSocketClientDao.pingPongChannelsMap.put(channelId , 1);
        }
    }

    /*
    *
    * 客户端存活，删除ping 消息发送列表
    *
    * */
    public void removePingClient (String channelId) {
        WebSocketClientDao.pingPongChannelsMap.remove(channelId);
    }

    public Collection<WebSocketClient> getPingClients (int pingTimes) {
        if (WebSocketClientDao.pingPongChannelsMap.isEmpty()) {
            return null;
        }
        Collection<WebSocketClient> clients = null;
        Set<String> channelIds = WebSocketClientDao.pingPongChannelsMap.keySet();
        if (channelIds != null) {
            clients = new ArrayList<>();
            for (String channelId : channelIds) {
                Integer mPingTimes = WebSocketClientDao.pingPongChannelsMap.get(channelId);
                //超过ping 限制次数
                if (mPingTimes.intValue() >= pingTimes) {
                    WebSocketClient webSocketClient = webSocketClientDao.getWebSocketClient(channelId);
                    clients.add(webSocketClient);
                }
            }
        }
        return clients;
    }


    public WebSocketClient removeWebSocketClient(String id ){
        return webSocketClientDao.removeChannelHandlerContext(id);
    }



    public WebSocketClientDao getWebSocketClientDao() {
        return webSocketClientDao;
    }

    public void setWebSocketClientDao(WebSocketClientDao webSocketClientDao) {
        this.webSocketClientDao = webSocketClientDao;
    }







}
