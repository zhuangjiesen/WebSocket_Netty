package com.jason.websocket.common;

import com.dragsun.websocket.client.WebSocketSession;
import com.dragsun.websocket.handler.websocket.AbstractWebSocketHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 通用的 netty - websocket 处理器
 * @Date: Created in 2018/5/12
 */
public class SimpleWebSocketHandler extends AbstractWebSocketHandler {


    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketFrame webSocketFrame) throws Exception {

        if (webSocketFrame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)webSocketFrame;
            String text = textWebSocketFrame.text();


            System.out.println(" 我是接收到的--------- : " + text );
            System.out.println(" thread : " + Thread.currentThread().getId() );

            TextWebSocketFrame response = new TextWebSocketFrame("我是返回信息... : " + System.currentTimeMillis());
            webSocketSession.sendMessage(response);
        }

    }


}
