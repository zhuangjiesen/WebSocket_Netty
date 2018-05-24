package com.dragsun.websocket.handler;


import com.dragsun.websocket.client.CloseStatus;
import com.dragsun.websocket.client.DefaultWebSocketSession;
import com.dragsun.websocket.client.WebSocketSession;
import com.dragsun.websocket.exception.WebSocketException;
import com.dragsun.websocket.handler.netty.AbstractInboundHandler;
import com.dragsun.websocket.handler.netty.AbstractOutboundHandler;
import com.dragsun.websocket.handler.websocket.SimpleWebSocketHandler;
import com.dragsun.websocket.handler.websocket.WebSocketHandler;
import com.dragsun.websocket.resolver.UpgradeResolver;
import com.dragsun.websocket.server.Configuration;
import com.dragsun.websocket.utils.ChannelUtil;
import com.dragsun.websocket.utils.LogUtils;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
*
*
* websocket channelHandler 请求处理工厂类
* 生成 WebSocketNettyServer 中的netty 的ChannelHandler 的处理类
* Created by zhuangjiesen on 2017/9/13.
*/
public class WebSocketChannelHandlerFactory  {

    private Configuration configuration;

    public ChannelInboundHandlerAdapter newWSInboundHandler() {
        ChannelInboundHandlerAdapter channelHandler = new WebSocketChannelHandlerFactory.WSInboundHandler(configuration.getWebSocketHandler());
        return channelHandler;
    }

    public ChannelOutboundHandlerAdapter newWSOutboundHandler() {
        ChannelOutboundHandlerAdapter outboundHandler = new WebSocketChannelHandlerFactory.WSOutboundHandler();
        return outboundHandler;
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    //全局websocket连接对象存储
    private static Map<String ,WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();


    /*
     * 消息事件处理
     * @author zhuangjiesen
     * @date 2018/5/14 上午10:07
     * @param
     * @return
     */
    private static class WSInboundHandler extends AbstractInboundHandler {

        // websocket 升级请求处理器
        private UpgradeResolver upgradeResolver = new UpgradeResolver();
        //默认请求处理器
        private WebSocketHandler webSocketHandler;

        public WSInboundHandler() {
            this.webSocketHandler = new SimpleWebSocketHandler();
        }

        public WSInboundHandler(WebSocketHandler webSocketHandler) {
            this.webSocketHandler = webSocketHandler;
        }


        @Override
        public void doChannelRead(String id , ChannelHandlerContext ctx, Object msg) throws Exception {
            // TODO Auto-generated method stub
            if (msg instanceof FullHttpRequest) {
                //处理http请求
                FullHttpRequest request = (FullHttpRequest) msg;
                String uri = request.uri();
                QueryStringDecoder uriDecoder = new QueryStringDecoder(uri);
                Map<String, List<String>> parameters = uriDecoder.parameters();
                String path = uriDecoder.path();
                String mURI = uriDecoder.uri();

                if (path.startsWith("//")) {
                    path = path.substring(1);
                }

                DefaultWebSocketSession webSocketSession = new DefaultWebSocketSession();
                webSocketSession.setParameters(parameters);
                webSocketSession.setHandshakeHeaders(request.headers());
                webSocketSession.setId(id);
                webSocketSession.setUri(new URI(path));
                webSocketSession.setChannelHandlerContext(ctx);
                //获取请求处理器
                WebSocketHandler socketHandler = this.getSocketHandler(path);
                if (socketHandler == null) {
                    webSocketSession.setWebSocketHandler(this.webSocketHandler);
                } else {
                    webSocketSession.setWebSocketHandler(webSocketHandler);
                }
                socketHandler = webSocketSession.getWebSocketHandler();

                //upgrade之前的回调 执行websocket前拦截，作参数的处理
                try {
                    socketHandler.beforeConnectionUpgraded(webSocketSession);
                } catch (Exception e) {
                    LogUtils.logError(this , e);
                    //处理异常 没有具体映射的请求处理器
                    socketHandler.handleTransportError(webSocketSession , e);
                    return ;
                }

                WebSocketServerHandshaker handshaker = null;
                // upgrade 与 websocket 握手过程
                if ((handshaker = upgradeResolver.handleRequest(ctx, request )) != null) {
                    webSocketSession.setWebSocketServerHandshaker(handshaker);
                    //完成后调用
                    socketHandler.afterConnectionEstablished(webSocketSession);
                    webSocketSessions.put(id , webSocketSession);
                } else {
                    socketHandler.handleTransportError(webSocketSession , new WebSocketException("upgrade failed.!"));
                }

            } else if (msg instanceof WebSocketFrame) {
                //处理websocket请求
                WebSocketSession webSocketSession = webSocketSessions.get(id);
                WebSocketHandler webSocketHandler = null;
                if (webSocketSession == null) {
                    webSocketHandler = webSocketHandler;
                } else if (msg instanceof CloseWebSocketFrame) {
                    //浏览器关闭后， WebSocket 对象会发送close ，此处进行处理
                    WebSocketSession wsSession = webSocketSessions.remove(id);
                    if (wsSession != null) {
                        wsSession.close();
                        wsSession.getWebSocketHandler().afterConnectionClosed(webSocketSession , CloseStatus.NORMAL);
                    }
                    KeepAliveHandlerAdapter.receivePongMessage(id);
                } else {
                    webSocketHandler = webSocketSession.getWebSocketHandler();
                    webSocketHandler.handleMessage(webSocketSession , (WebSocketFrame) msg);
                }
                KeepAliveHandlerAdapter.receivePongMessage(id);
            } else {
                throw new RuntimeException("无法处理的请求");
            }
        }


        @Override
        public void doExceptionCaught(String channelId , ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // TODO Auto-generated method stub
            LogUtils.logError(this, cause );
            WebSocketSession webSocketSession = webSocketSessions.get(channelId);
            if (webSocketSession != null) {
                webSocketSession.getWebSocketHandler().handleTransportError(webSocketSession , cause);
            }
        }


        public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
            this.webSocketHandler = webSocketHandler;
        }

        public WebSocketHandler getWebSocketHandler() {
            return webSocketHandler;
        }

        public UpgradeResolver getUpgradeResolver() {
            return upgradeResolver;
        }

        public void setUpgradeResolver(UpgradeResolver upgradeResolver) {
            this.upgradeResolver = upgradeResolver;
        }

        private WebSocketHandler getSocketHandler(String uri){
            if (webSocketHandler != null) {
                return webSocketHandler;
            }
            return null;
        }
    }





    /*
     * 关闭连接操作
     * @author zhuangjiesen
     * @date 2018/5/14 上午10:06
     * @param
     * @return
     */
    private static class WSOutboundHandler extends AbstractOutboundHandler {

        @Override
        public void doDisconnect(String channelId ,ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        }

        @Override
        public void doClose(String channelId ,ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            WebSocketSession webSocketSession = webSocketSessions.remove(channelId);
            if (webSocketSession != null) {
                webSocketSession.close();
                webSocketSession.getWebSocketHandler().afterConnectionClosed(webSocketSession ,  CloseStatus.NORMAL);
            }
            KeepAliveHandlerAdapter.receivePongMessage(channelId);
        }



    }





    /**
     * 服务端保活清除过期或者失联连接 处理器
     *  微信浏览器打开页面关闭后(或者另外的场景)不会触发close消息 ，所以又做了一层心跳机制
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
    private static class KeepAliveHandlerAdapter {

        //轮训时间 检测过期连接 定时器定时时间
        private final static int SCHEDULE_SECONDS = 30;
        private static ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);

        /*标记状态*/
        private static volatile boolean isSent = true;

        /** 允许保活次数， 超过这个数值认为失联，清理连接**/
        private static volatile int MAX_RE_PING = 5;
        //心跳  已发送次数
        private static ConcurrentHashMap<String , Integer> pingPongChannelsMap = new ConcurrentHashMap<>();


        static {
            scheduleService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    LogUtils.logDebug(this, "  保活，清理线程启动.... ");
                    try {
                        if (isSent) {
                            isSent = false;
                            //定时发送心跳
                            sendPingMessageToAll();

                        } else {
                            isSent = true;
                            clearNotPingPongMessage();
                        }
                    } catch (Exception e) {
                        LogUtils.logError(this , e);
                    }
                }
            } , 1L , SCHEDULE_SECONDS , TimeUnit.SECONDS);
        }


        /*
        * 给所有客户端发送ping 消息
        *
        * */
        public static void sendPingMessageToAll(){
            Collection<WebSocketSession> sessions = WebSocketChannelHandlerFactory.webSocketSessions.values();;
            if (sessions != null) {
                for (WebSocketSession socketSession : sessions) {
                    PingWebSocketFrame ping = new PingWebSocketFrame();
                    socketSession.sendMessage(ping);
                    Integer pingTimes = KeepAliveHandlerAdapter.pingPongChannelsMap.get(socketSession.getId());
                    if (pingTimes != null) {
                        KeepAliveHandlerAdapter.pingPongChannelsMap.put(socketSession.getId() , pingTimes.intValue() + 1);
                    } else {
                        KeepAliveHandlerAdapter.pingPongChannelsMap.put(socketSession.getId() , 1);
                    }
                }
            }
        }



        /*
        * 清理上次保活操作发送ping 消息得不到反馈的连接
        *
        *
        * */
        public static void clearNotPingPongMessage(){
            Collection<WebSocketSession> sessions = WebSocketChannelHandlerFactory.webSocketSessions.values();;
            if (sessions != null) {
                for (WebSocketSession socketSession : sessions) {
                    String id = socketSession.getId();
                    Integer pingTimes = KeepAliveHandlerAdapter.pingPongChannelsMap.get(id);
                    if (pingTimes != null && pingTimes.intValue() >= MAX_RE_PING) {
                        //断开连接
                        socketSession.sendMessage(new CloseWebSocketFrame());

                        //清除客户端对象
                        KeepAliveHandlerAdapter.pingPongChannelsMap.remove(id);
                        webSocketSessions.remove(id);
                    }
                }
            }
        }





        /*
        * 收到pong 消息，表示连接存活
        *
        *
        * */
        public static void receivePongMessage(String id){
            KeepAliveHandlerAdapter.pingPongChannelsMap.remove(id);
        }


    }






}
