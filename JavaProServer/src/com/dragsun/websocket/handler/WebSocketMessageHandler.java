package com.dragsun.websocket.handler;

import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * Created by zhuangjiesen on 2017/8/9.
 */
public interface WebSocketMessageHandler {


    public void onMessage(WebSocketFrame webSocketFrame);

}
