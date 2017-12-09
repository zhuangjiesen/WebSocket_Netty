package com.dragsun.websocket.protocol;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.adapter.SubprotocolHandlerAdapter;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.mapping.WSRequestHandlerMapping;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerAdapter;

import java.util.Map;

/**
 *  websocket 子协议处理器
 *  封装了一层 接收和推送的方法，子类可以选择继承 AbstractProtocolHandler
 *
 *  实现 handleRecievedMessage 接收客户端的消息
 *  实现 handlePushedMessage 处理推送消息,
 *      实现推送的方法是调用  pushMessage() 这个方法内回去调用 handlePushedMessage() 方法获取消息内容推送到前端
 *  实现 handleCompletedProtocolRegisty 处理握手成功事件
 *
 *  都可选，避免实现接口情况下每个方法都需要去实现
 *
 * Created by zhuangjiesen on 2017/11/16.
 */
public abstract class AbstractProtocolHandler implements WSProtocolHandler , ApplicationContextAware  {
    protected ApplicationContext applicationContext;

    /*
    *
    * 返回值是推送消息
    * 通过调用 pushMessage() 可以推到客户段
    *
    * */
    @Override
    public String wrapperPushedMessage(Map<String, Object> params) {
        return handlePushedMessage(params);
    }

    protected String handlePushedMessage(Map<String, Object> params){
        return null;
    }

    @Override
    public void onMessageRecieved(ChannelHandlerContext ctx, JSONObject message) {
        handleRecievedMessage(ctx , message);
    }


    protected void handleRecievedMessage(ChannelHandlerContext ctx, JSONObject message){
    }



    /*
    * 这里处理握手成功时的回调
    *
    * */
    @Override
    public void onProtocolRegistyCompleted(ChannelHandlerContext ctx, WebSocketClient webSocketClient) {
        handleCompletedProtocolRegisty(ctx , webSocketClient);
    }



    protected void handleCompletedProtocolRegisty(ChannelHandlerContext ctx, WebSocketClient webSocketClient){
    }




    /*
        * 发送数据
        *
        * */
    public void pushMessage(Map<String , Object> params) {
        String message = wrapperPushedMessage(params);
        if (StringUtils.hasLength(message)) {
            String protocol = MessageUtils.getProtocol(this);
            String uri = MessageUtils.getUri(this);
            SubprotocolHandlerAdapter handlerAdapter = (SubprotocolHandlerAdapter)applicationContext.getBean(WSRequestHandlerMapping.class).getFrameHandlerAdapterByUri(uri);
            if (handlerAdapter != null) {
                handlerAdapter.sendMessage(protocol , message );
            }
        }
    }


    /*
* 发送数据
*
* */
    public void pushMessage(String message) {
        if (StringUtils.hasLength(message)) {
            String protocol = MessageUtils.getProtocol(this);
            String uri = MessageUtils.getUri(this);
            SubprotocolHandlerAdapter handlerAdapter = (SubprotocolHandlerAdapter)applicationContext.getBean(WSRequestHandlerMapping.class).getFrameHandlerAdapterByUri(uri);
            if (handlerAdapter != null) {
                handlerAdapter.sendMessage(protocol ,message );
            }
        }

    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }


}
