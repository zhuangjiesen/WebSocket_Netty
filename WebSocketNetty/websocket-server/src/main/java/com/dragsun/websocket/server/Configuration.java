package com.dragsun.websocket.server;

import com.dragsun.websocket.handler.websocket.WebSocketHandler;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:配置类
 * @Date: Created in 2018/5/15
 */
public class Configuration {


    private WebSocketHandler webSocketHandler;


    public WebSocketHandler getWebSocketHandler() {
        return webSocketHandler;
    }

    public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }
}
