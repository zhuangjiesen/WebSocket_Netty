package com.java.core.netty.websocket.adapter;

import com.java.core.netty.websocket.cache.WebSocketCacheManager;
import com.java.core.netty.websocket.cache.WebSocketClient;
import com.java.core.netty.websocket.utils.MessageUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务端保活清除过期或者失联连接 处理器
 *
 *
 * 策略是通过定时器
 * 1.遍历客户端发送ping消息 ，然后在缓存中 pingPongMap  标记 1已发送 (或者n 表示发送次数)
 * 2.客户端接收ping 消息会返回pong 消息 (websocket协议标准 见RFC6455)
 * 3.在接收端 doHandleRequest() 方法中(接收到非close frame )说明客户端有反馈，表示存活，删除 pingPongMap
 * 4.接着下一个周期定时器去遍历 pingPongMap 查出已经发送ping 无响应超过 MAX_RE_PING 次数
 * 5.删除超过 MAX_RE_PING 无响应的客户端
 * 6.每次接收到客户端的消息，都会调用 doHandleRequest() 方法清除 pingPongMap 表示客户端存活
 *
 * Created by zhuangjiesen on 2017/9/14.
 */
public abstract class KeepAliveHandlerAdapter<T extends WebSocketFrame> extends AbstractFrameHandlerAdapter<T> implements ApplicationContextAware ,InitializingBean {

    public static ApplicationContext mApplicationContext;

    //轮训时间 检测过期连接 定时器定时时间
    private final static int SCHEDULE_SECONDS = 60;
    private static ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);

    /*标记状态*/
    private static volatile boolean isSent = true;

    /** 允许保活次数， 超过这个数值认为失联，清理连接**/
    private static volatile int MAX_RE_PING = 10;


    static {
        scheduleService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println(" 保活，清理线程启动....");
                try {
                    if (mApplicationContext != null) {
                        if (isSent) {
                            isSent = false;
                            //定时发送心跳
                            sendPingMessageToAll();

                        } else {
                            isSent = true;
                            clearNotPingPongMessage();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } , 1L , SCHEDULE_SECONDS , TimeUnit.SECONDS);
    }


    /*
    * 给所有客户端发送ping 消息
    *
    * */
    public static void sendPingMessageToAll(){
        WebSocketCacheManager webSocketCacheManager = mApplicationContext.getBean(WebSocketCacheManager.class);
        Collection<WebSocketClient> clients = webSocketCacheManager.getAllClients();
        if (clients != null) {
            for (WebSocketClient client : clients) {
                MessageUtils.sendPingMessage(client);
                webSocketCacheManager.putPingClient(client.getChannelHandlerContext().channel().id().asLongText());
            }
        }
    }



    /*
    * 清理上次保活操作发送ping 消息得不到反馈的连接
    *
    *
    * */
    public static void clearNotPingPongMessage(){
        WebSocketCacheManager webSocketCacheManager = mApplicationContext.getBean(WebSocketCacheManager.class);
        Collection<WebSocketClient> clients = webSocketCacheManager.getPingClients(MAX_RE_PING);
        if (clients != null) {
            for (WebSocketClient client : clients) {
                Channel channel = client.getChannelHandlerContext().channel();
                if (channel.isOpen()) {
                    client.getHandshaker().close(channel , new CloseWebSocketFrame());
                }
                webSocketCacheManager.removeWebSocketClient(channel.id().asLongText());
            }
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        super.setApplicationContext(context);

        mApplicationContext = context;
    }


    @Override
    public void doHandleRequest(ChannelHandlerContext ctx, Object msg, WebSocketClient webSocketClient) {
        super.doHandleRequest(ctx, msg, webSocketClient);
        WebSocketCacheManager webSocketCacheManager = applicationContext.getBean(WebSocketCacheManager.class);
        //每次接收到前端数据
        if (!CloseWebSocketFrame.class.equals(webSocketClient.getClass())) {
            webSocketCacheManager.removePingClient(ctx.channel().id().asLongText());
        } else {
            webSocketCacheManager.removeWebSocketClient(ctx.channel().id().asLongText());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }



}
