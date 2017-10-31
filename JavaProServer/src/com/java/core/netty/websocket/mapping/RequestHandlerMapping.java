package com.java.core.netty.websocket.mapping;

import com.java.core.netty.websocket.adapter.HandlerAdapter;
import com.java.core.netty.websocket.annotation.RequestMapping;
import com.java.core.netty.websocket.cache.WebSocketCacheManager;
import com.java.core.netty.websocket.cache.WebSocketClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class RequestHandlerMapping implements InitializingBean , ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static ConcurrentHashMap<String , HandlerAdapter> uriAndHandlerAdapterMap = new ConcurrentHashMap<>();

    /*
    * 获取请求uri 绑定的请求处理器
    *
    * */
    public HandlerAdapter getFrameHandlerAdapterByUri(String uri) {
        return uriAndHandlerAdapterMap.get(uri);
    }



    /*
    * 获取当前连接绑定的请求处理器
    *
    * */
    public HandlerAdapter getFrameHandlerAdapterById(String id) {
        WebSocketCacheManager webSocketCacheManager = applicationContext.getBean(WebSocketCacheManager.class);
        WebSocketClient webSocketClient = webSocketCacheManager.getWebSocketClient(id);
        if (webSocketClient != null) {
            return webSocketClient.getHandlerAdapter();
        }
        return null;
    }



    /*
    *
    * 为请求注册请求处理器
    *
    * */
    public HandlerAdapter registHandlerAdapter(String uri , String id ) {
        HandlerAdapter handlerAdapter = null;
        if ((handlerAdapter = getFrameHandlerAdapterByUri(uri)) == null) {
            throw new RuntimeException("未找到合适的请求处理器");
        }
        return handlerAdapter;
    }


    /*
    *
    * 请求处理器初始化
    *
    * */
    public void initHandlerAdapter(String uri , HandlerAdapter handlerAdapter ) {
        uriAndHandlerAdapterMap.put(uri , handlerAdapter);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        /*
        *
        * 初始化请求处理器
        * 把uri 对应的请求处理器加载到内存
        * */
        String[] names = applicationContext.getBeanNamesForType(HandlerAdapter.class);
        if (names != null && names.length > 0) {
            for (String name : names ) {
                HandlerAdapter handlerAdapter = applicationContext.getBean(name ,HandlerAdapter.class );
                RequestMapping requestMapping = handlerAdapter.getClass().getAnnotation(RequestMapping.class);
                if (requestMapping != null) {
                    String uri = requestMapping.uri();
                    initHandlerAdapter(uri , handlerAdapter);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
