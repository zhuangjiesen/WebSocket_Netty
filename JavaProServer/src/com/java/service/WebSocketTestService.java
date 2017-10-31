package com.java.service;


import com.java.core.netty.websocket.adapter.HandlerAdapter;
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
                    HandlerAdapter frameHandlerAdapter = applicationContext.getBean("frameHandlerAdapter" , HandlerAdapter.class);
                    Map<String , Object> frameParams = new HashMap();
                    String frameMessage = "我是默认业务推送的数据" + System.currentTimeMillis();
                    frameParams.put("message" , frameMessage);
                    frameHandlerAdapter.handleResponse(frameParams);


                    //定位推送
                    HandlerAdapter locationHandlerAdapter = applicationContext.getBean("locationHandlerAdapter" , HandlerAdapter.class);
                    Map<String , Object> locParams = new HashMap();
                    String locMessage = "我是location业务推送的数据" + System.currentTimeMillis();
                    locParams.put("message" , locMessage);
                    locationHandlerAdapter.handleResponse(locParams);




                    HandlerAdapter chatOnlineListHandlerAdapter = applicationContext.getBean("chatOnlineListHandlerAdapter" , HandlerAdapter.class);
                    chatOnlineListHandlerAdapter.handleResponse(null);


                    HandlerAdapter chatHandlerAdapter = applicationContext.getBean("chatHandlerAdapter" , HandlerAdapter.class);
                    chatHandlerAdapter.handleResponse(null);

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
