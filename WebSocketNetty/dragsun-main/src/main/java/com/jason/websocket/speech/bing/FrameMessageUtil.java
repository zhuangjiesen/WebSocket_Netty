package com.jason.websocket.speech.bing;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.client.WebSocketSession;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/23
 */
public class FrameMessageUtil {


    public static void sendMessage(WebSocketSession webSocketSession , int type , String content) {
        JSONObject text = new JSONObject();
        text.put("type" , type);
        text.put("content", content);
        TextWebSocketFrame tm = new TextWebSocketFrame(text.toString());
        webSocketSession.sendMessage(tm);

    }
}
