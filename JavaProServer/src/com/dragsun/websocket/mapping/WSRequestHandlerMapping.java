package com.dragsun.websocket.mapping;

import com.dragsun.websocket.adapter.SubprotocolHandlerAdapter;
import com.dragsun.websocket.adapter.WSHandlerAdapter;
import com.dragsun.websocket.annotation.WSProtocol;
import com.dragsun.websocket.annotation.WSRequestMapping;
import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.cache.WebSocketClient;
import com.dragsun.websocket.constant.WebSocketConstant;
import com.dragsun.websocket.protocol.WSProtocolHandler;
import com.dragsun.websocket.utils.MessageUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
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

                    //初始化带子协议的请求处理器
                    String[] protocolHandlerNames = applicationContext.getBeanNamesForType(WSProtocolHandler.class);
                    if (protocolHandlerNames != null && protocolHandlerNames.length > 0) {
                        for (String protocolHandlerName : protocolHandlerNames ) {
                            WSProtocolHandler protocolHandler = applicationContext.getBean(protocolHandlerName ,WSProtocolHandler.class );
                            WSProtocol protocolAnnotation = protocolHandler.getClass().getAnnotation(WSProtocol.class);
                            if (protocolAnnotation != null) {
                                // 已经有单uri 请求的处理器了 WSHandlerAdapter
                                String uri = protocolAnnotation.uri();
                                SubprotocolHandlerAdapter subprotocolHandlerAdapter = null;
                                WSHandlerAdapter handlerAdapter = uriAndHandlerAdapterMap.get(uri);
                                if (handlerAdapter == null) {
                                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                                    beanDefinition.setBeanClass(SubprotocolHandlerAdapter.class);
                                    String beanName = uri + "_" + SubprotocolHandlerAdapter.class.getName();
                                    beanRegistry.registerBeanDefinition(beanName ,beanDefinition );
                                    subprotocolHandlerAdapter = applicationContext.getBean(beanName ,SubprotocolHandlerAdapter.class );
                                    uriAndHandlerAdapterMap.put(uri , subprotocolHandlerAdapter);
                                } else if (handlerAdapter instanceof SubprotocolHandlerAdapter) {
                                    subprotocolHandlerAdapter = (SubprotocolHandlerAdapter)handlerAdapter;
                                } else {
                                    throw new RuntimeException("已经有uri : " + uri + " 的请求处理器");
                                }
                                subprotocolHandlerAdapter.addProtocolHandler(protocolHandler);

                                if ((subprotocolHandlerAdapter = (SubprotocolHandlerAdapter)uriAndHandlerAdapterMap.get(uri)) == null) {
                                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                                    beanDefinition.setBeanClass(SubprotocolHandlerAdapter.class);
                                    String beanName = uri + "_" + SubprotocolHandlerAdapter.class.getName();
                                    beanRegistry.registerBeanDefinition(beanName ,beanDefinition );
                                    subprotocolHandlerAdapter = applicationContext.getBean(beanName ,SubprotocolHandlerAdapter.class );
                                    uriAndHandlerAdapterMap.put(uri , subprotocolHandlerAdapter);
                                }
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
    public void registHandlerAdapter(FullHttpRequest request , String channelId ,  WebSocketClient webSocketClient ) {
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
            if (StringUtils.hasLength(protocols)) {
                handlerAdapter = uriAndHandlerAdapterMap.get(uri);
                if (handlerAdapter != null) {
                    if (handlerAdapter instanceof SubprotocolHandlerAdapter) {
                        SubprotocolHandlerAdapter subHandlerAdapter = (SubprotocolHandlerAdapter)handlerAdapter;
                        String[] protocolArr = protocols.split(",");
                        webSocketClient.setProtocols(protocolArr);
                        for (String protocol : protocolArr) {
                            if (subHandlerAdapter.getProtocolHandler(protocol.trim()) == null) {
                                throw new RuntimeException("未找到合适的请求处理器 protocol : " + protocol);
                            }
                        }
                    } else {
                        throw new RuntimeException("未找到合适的请求处理器 : " + uri);
                    }
                }
            } else {
                if ((handlerAdapter = getFrameHandlerAdapterByUri(uri)) == null) {
                    throw new RuntimeException("未找到合适的请求处理器 : " + uri);
                }
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
