package com.dragsun.websocket.adapter;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.protocol.WSProtocolHandler;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 用来处理子协议的默认处理器
 * Created by zhuangjiesen on 2017/9/13.
 */
public class SubprotocolHandlerAdapter extends KeepAliveHandlerAdapter<TextWebSocketFrame> {

    public ConcurrentHashMap<String , ConcurrentHashMap<String , WebSocketClient>> protocolClientsMap = new ConcurrentHashMap<>();


    private ConcurrentHashMap<String , WSProtocolHandler> protocolsMap = new ConcurrentHashMap<>();

    public void addProtocolHandler( WSProtocolHandler protocolHandler){
        String protocol = MessageUtils.getProtocol(protocolHandler);
        if (protocol.startsWith("/")) {
            protocol = protocol.substring(1);
        }
        protocolsMap.put(protocol , protocolHandler );
    }


    public WSProtocolHandler getProtocolHandler(String protocol) {
        WSProtocolHandler protocolHandler = null;
        protocolHandler = protocolsMap.get(protocol);
        return protocolHandler;
    }


    public void sendMessage(String protocol , String message) {
        if (message == null || message.length() == 0) {
            return ;
        }
        if (protocol.startsWith("/")) {
            protocol = protocol.substring(1);
        }
        ConcurrentHashMap<String , WebSocketClient > clients = protocolClientsMap.get(protocol);
        if (clients != null && clients.size() > 0) {
            JSONObject jsonMsg = new JSONObject();
            jsonMsg.put("protocol" , protocol);
            jsonMsg.put("message" , message);
            String msg = jsonMsg.toString();
            Collection<WebSocketClient> clientsList = null;
            clientsList = clients.values();
            for (WebSocketClient socketClient : clientsList) {
                String channelId = socketClient.getChannelHandlerContext().channel().id().asLongText();
                //判断是否下线,清除相应的客户端
                if (socketClient.isClosed()) {
                    clients.remove(channelId);
                } else {
                    MessageUtils.sendMessage(socketClient , msg);
                }
            }
        }
    }




    @Override
    public void handleResponse(Map<String , Object> params) {
    }



    @Override
    public void onUpgradeCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
        String[] protocols = webSocketClient.getProtocols();
        String channelId = ctx.channel().id().asLongText();
        for (String protocol : protocols) {
            protocol = protocol.trim();
            ConcurrentHashMap<String , WebSocketClient > clients = protocolClientsMap.get(protocol);
            if (clients == null) {
                synchronized (protocolClientsMap) {
                    if (clients == null) {
                        clients = new ConcurrentHashMap<String , WebSocketClient >();
                        protocolClientsMap.put(protocol , clients);
                    }
                }
            }
            clients.put(channelId , webSocketClient);

            // 触发事件
            WSProtocolHandler protocolHandler = protocolsMap.get(protocol);
            if (protocolHandler != null) {
                protocolHandler.onProtocolRegistyCompleted(ctx , webSocketClient);
            }
        }



    }


    /*
    * 报文格式:
    *
    * {
    *   'protocols' :  'xxx' ,
    *   'message' : '' ,
    *
    *   ...(其他数据)
    * }
    *
    *
    * */
    @Override
    public void handlerWebSocketFrameData(ChannelHandlerContext ctx, TextWebSocketFrame webSocketFrame) {
        WSProtocolHandler protocolHandler = null;
        String text = webSocketFrame.text();
        if (!text.equals("ping")) {
            JSONObject textJson = (JSONObject) JSONObject.parse(text);
            String protocols = textJson.getString("protocols");
            if (StringUtils.hasLength(protocols)) {
                String[] protocolsArr = protocols.split(",");
                for (String protocol : protocolsArr) {
                    protocolHandler = protocolsMap.get(protocol);
                    protocolHandler.onMessageRecieved(ctx , textJson);
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
        Enumeration<String> keys = protocolsMap.keys();
        if (keys != null) {
            while (keys.hasMoreElements()) {
                String protocol = keys.nextElement();
                ConcurrentHashMap<String , WebSocketClient > clients = protocolClientsMap.get(protocol);
                if (clients != null) {
                    clients.remove(ctx.channel().id().asLongText());
                }
            }
        }
    }


}
