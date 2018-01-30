package com.dragsun.websocket.mapping;

import com.dragsun.websocket.adapter.TopicHandlerAdapter;
import com.dragsun.websocket.adapter.WSHandlerAdapter;
import com.dragsun.websocket.annotation.WSRequestMapping;
import com.dragsun.websocket.annotation.WSTopic;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.constant.WebSocketConstant;
import com.dragsun.websocket.topic.WSTopicHandler;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class WSRequestHandlerMapping implements  ApplicationContextAware , ApplicationListener<ContextRefreshedEvent> ,BeanDefinitionRegistryPostProcessor {

    private static ApplicationContext applicationContext;
    private static BeanDefinitionRegistry beanRegistry;
    private static ConfigurableListableBeanFactory beanFactory;

    private static ConcurrentHashMap<String , WSHandlerAdapter> uriAndHandlerAdapterMap = new ConcurrentHashMap<>();


    private volatile boolean isInit = false;
    /*
    * 获取请求uri 绑定的请求处理器
    *
    * */
    public WSHandlerAdapter getFrameHandlerAdapterByUri(String uri) {
        if (!uri.startsWith("/")) {
            uri = "/".concat(uri.trim());
        }
        return uriAndHandlerAdapterMap.get(uri);
    }


    public void init() {
        if (!isInit) {
            synchronized (this) {
                if (!isInit) {
                    /*
                    *
                    * 初始化请求处理器
                    * 把uri 对应的请求处理器加载到内存
                    * */
                    String[] handlerAdapterNames = applicationContext.getBeanNamesForType(WSHandlerAdapter.class);
                    if (handlerAdapterNames != null && handlerAdapterNames.length > 0) {
                        for (String handlerAdapterName : handlerAdapterNames ) {
                            WSHandlerAdapter handlerAdapter = applicationContext.getBean(handlerAdapterName ,WSHandlerAdapter.class );
                            WSRequestMapping requestMapping = handlerAdapter.getClass().getAnnotation(WSRequestMapping.class);
                            if (requestMapping != null) {
                                String uri = requestMapping.uri();
                                initHandlerAdapter(uri , handlerAdapter);
                            }
                        }
                    }


                    String[] topicHandlerNames = applicationContext.getBeanNamesForType(WSTopicHandler.class);
                    if (topicHandlerNames != null && topicHandlerNames.length > 0) {
                        for (String topicHandlerName : topicHandlerNames ) {
                            WSTopicHandler topicHandler = applicationContext.getBean(topicHandlerName ,WSTopicHandler.class );

                            WSTopic topicAnnotation = topicHandler.getClass().getAnnotation(WSTopic.class);
                            if (topicAnnotation != null) {
                                // 已经有单uri 请求的处理器了 WSHandlerAdapter
                                String uri = topicAnnotation.uri();
                                String topic = topicAnnotation.topic();

                                TopicHandlerAdapter topicHandlerAdapter = null;
                                WSHandlerAdapter handlerAdapter = uriAndHandlerAdapterMap.get(uri);
                                if (handlerAdapter == null) {
                                    //在spring 中注册一个 WSHandlerAdapter 的 bean
                                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                                    beanDefinition.setBeanClass(TopicHandlerAdapter.class);
                                    String beanName = uri + "_" + TopicHandlerAdapter.class.getName();
                                    beanRegistry.registerBeanDefinition(beanName ,beanDefinition );
                                    topicHandlerAdapter = applicationContext.getBean(beanName ,TopicHandlerAdapter.class );
                                    uriAndHandlerAdapterMap.put(uri , topicHandlerAdapter);
                                } else if (handlerAdapter instanceof TopicHandlerAdapter) {
                                    topicHandlerAdapter = (TopicHandlerAdapter)handlerAdapter;
                                } else {
                                    throw new RuntimeException("已经有uri : " + uri + " 的请求处理器");
                                }
                                topicHandlerAdapter.subscribeHandler(topic ,topicHandler );
                            }
                        }
                    }
                    isInit = true;
                }
            }
        }
    }



    /*
    * 获取当前连接绑定的请求处理器
    *
    * */
    public WSHandlerAdapter getFrameHandlerAdapterById(String id) {
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
    public void registHandlerAdapter(FullHttpRequest request , WebSocketClient webSocketClient ) {
        init();
        HttpHeaders httpHeaders = request.headers();
        String protocols = httpHeaders.get( WebSocketConstant.SEC_WEBSOCKET_PROTOCOL);
        if (protocols == null || protocols.equals("null")) {
            protocols = "";
        }
        String uri = request.uri();
        uri = MessageUtils.getHttpGetUri(uri);
        WSHandlerAdapter handlerAdapter = null;
        if (StringUtils.hasLength(uri)) {
            if ((handlerAdapter = getFrameHandlerAdapterByUri(uri)) == null) {
                throw new RuntimeException("未找到合适的请求处理器 : " + uri);
            }
        }
        webSocketClient.setHandlerAdapter(handlerAdapter);
    }


    /*
    *
    * 请求处理器初始化
    *
    * */
    public void initHandlerAdapter(String uri , WSHandlerAdapter handlerAdapter ) {
        uriAndHandlerAdapterMap.put(uri , handlerAdapter);
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanRegistry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory mBeanFactory) throws BeansException {
        this.beanFactory = mBeanFactory;



    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            init();
        }
    }

}
