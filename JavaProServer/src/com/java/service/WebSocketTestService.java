package com.java.service;


import com.java.core.netty.websocket.adapter.WSHandlerAdapter;
import com.java.core.netty.websocket.common.protocols.IndexProtocolHandler;
import com.java.core.netty.websocket.common.protocols.LocationProtocolHandler;
import com.java.core.netty.websocket.common.protocols.NewsProtocolHandler;
import com.java.core.netty.websocket.common.protocols.StockInfoProtocolHandler;
import com.java.core.netty.websocket.protocol.WSProtocolHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhuangjiesen on 2017/8/15.
 */
public class WebSocketTestService implements InitializingBean , ApplicationContextAware{

    private static ApplicationContext applicationContext;

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public void pushFrame(){}
    public void pushLocation(){}

    @Override
    public void afterPropertiesSet() throws Exception {
        //测试发送消息
        System.out.println("WebSocketTestService .....start....");

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("WebSocketTestService .....定时发送消息....");

                try {


                    //默认推送
                    {
                        WSHandlerAdapter frameHandlerAdapter = applicationContext.getBean("frameHandlerAdapter" , WSHandlerAdapter.class);
                        Map<String , Object> frameParams = new HashMap();
                        String frameMessage = "我是 frameHandlerAdapter 默认业务推送的数据" + System.currentTimeMillis();
                        frameParams.put("message" , frameMessage);
                        frameHandlerAdapter.handleResponse(frameParams);
                    }

                    {
                        //定位推送
                        WSHandlerAdapter locationHandlerAdapter = applicationContext.getBean("locationHandlerAdapter" , WSHandlerAdapter.class);
                        Map<String , Object> locParams = new HashMap();
                        String locMessage = "我是 locationHandlerAdapter 业务推送的数据" + System.currentTimeMillis();
                        locParams.put("message" , locMessage);
                        locationHandlerAdapter.handleResponse(locParams);
                    }


                    {
                        IndexProtocolHandler protocolHandler = applicationContext.getBean(IndexProtocolHandler.class);
                        Map<String , Object> frameParams = new HashMap();
                        String frameMessage = "我是 IndexProtocolHandler 业务推送的数据" + System.currentTimeMillis();
                        frameParams.put("message" , frameMessage);
                        protocolHandler.pushMessage(frameParams);
                    }

                    {
                        LocationProtocolHandler protocolHandler = applicationContext.getBean(LocationProtocolHandler.class);
                        Map<String , Object> frameParams = new HashMap();
                        String frameMessage = "我是 LocationProtocolHandler 业务推送的数据" + System.currentTimeMillis();
                        frameParams.put("message" , frameMessage);
                        protocolHandler.pushMessage(frameParams);
                    }
                    {
                        NewsProtocolHandler protocolHandler = applicationContext.getBean(NewsProtocolHandler.class);
                        Map<String , Object> frameParams = new HashMap();
                        String frameMessage = "我是 NewsProtocolHandler 业务推送的数据" + System.currentTimeMillis();
                        frameParams.put("message" , frameMessage);
                        protocolHandler.pushMessage(frameParams);
                    }
                    {
                        StockInfoProtocolHandler protocolHandler = applicationContext.getBean(StockInfoProtocolHandler.class);
                        Map<String , Object> frameParams = new HashMap();
                        String frameMessage = "我是 StockInfoProtocolHandler 业务推送的数据" + System.currentTimeMillis();
                        frameParams.put("message" , frameMessage);
                        protocolHandler.pushMessage(frameParams);
                    }



//                    WSHandlerAdapter chatOnlineListHandlerAdapter = applicationContext.getBean("chatOnlineListHandlerAdapter" , WSHandlerAdapter.class);
//                    chatOnlineListHandlerAdapter.handleResponse(null);


//                    WSHandlerAdapter chatHandlerAdapter = applicationContext.getBean("chatHandlerAdapter" , WSHandlerAdapter.class);
//                    chatHandlerAdapter.handleResponse(null);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } , 1L , 10 , TimeUnit.SECONDS);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }



}
