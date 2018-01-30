package com.java.service;


import com.dragsun.websocket.adapter.WSHandlerAdapter;
import com.dragsun.websocket.utils.MessageUtils;
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

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

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
                    {
                        String frameMessage = "我是 index 默认业务推送的数据" + System.currentTimeMillis();
                        MessageUtils.sendMessage("index" , frameMessage );
                    }

                    {
                        String frameMessage = "我是 location 默认业务推送的数据" + System.currentTimeMillis();
                        MessageUtils.sendMessage("location" ,frameMessage);
                    }

                    {
                        String frameMessage = "我是 news 默认业务推送的数据" + System.currentTimeMillis();
                        MessageUtils.sendMessage("news" ,frameMessage);
                    }

                    {
                        String frameMessage = "我是 stockInfo 默认业务推送的数据" + System.currentTimeMillis();
                        MessageUtils.sendMessage("stockInfo" , frameMessage);
                    }




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



//                    WSHandlerAdapter chatOnlineListHandlerAdapter = applicationContext.getBean("chatOnlineListHandlerAdapter" , WSHandlerAdapter.class);
//                    chatOnlineListHandlerAdapter.handleResponse(null);


//                    WSHandlerAdapter chatHandlerAdapter = applicationContext.getBean("chatHandlerAdapter" , WSHandlerAdapter.class);
//                    chatHandlerAdapter.handleResponse(null);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } , 1L , 5 , TimeUnit.SECONDS);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }



}
