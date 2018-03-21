package com.dragsun.websocket.server;

import com.dragsun.websocket.cache.WebSocketCacheManager;
import com.dragsun.websocket.mapping.WSRequestHandlerMapping;
import com.dragsun.websocket.resolver.UpgradeResolver;
import com.dragsun.websocket.utils.LogUtils;
import com.dragsun.websocket.handler.WebSocketChannelHandlerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * netty websocket 服务器
 *
 * Created by zhuangjiesen on 2017/8/8.
 */
public class WebSocketNettyServer implements InitializingBean , ApplicationContextAware , BeanDefinitionRegistryPostProcessor{

    /** 端口号 **/
    private int port;
    private WebSocketChannelHandlerFactory webSocketChannelHandlerFactory;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private BeanDefinitionRegistry beanDefinitionRegistry;
    private ApplicationContext applicationContext ;

    public WebSocketNettyServer() {
        super();
        // TODO Auto-generated constructor stub
    }


    public void startNettyServer() {
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, 1024); //连接数
                    bootstrap.option(ChannelOption.TCP_NODELAY, true);  //不延迟，消息立即发送
//		            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);  //超时时间
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //长连接
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel)
                                throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();

                            p.addLast("http-codec", new HttpServerCodec());
                            p.addLast("aggregator", new HttpObjectAggregator(65536));
                            p.addLast("http-chunked", new ChunkedWriteHandler());
                            //请求处理
                            p.addLast("inboundHandler", webSocketChannelHandlerFactory.newWebSocketChannelHandler());
                            //关闭处理
                            p.addLast("outboundHandler", webSocketChannelHandlerFactory.newWebSocketOutboundChannelHandler());
                        }
                    });


                    ChannelFuture f = bootstrap.bind(port).sync();
                    if (f.isSuccess()) {
                    }
                    f.channel().closeFuture().sync();
                } catch (Exception e) {
                    LogUtils.logError(this, e );
                } finally {
                    boss.shutdownGracefully();
		            worker.shutdownGracefully();
                }
            }
        });


    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public WebSocketChannelHandlerFactory getWebSocketChannelHandlerFactory() {
        return webSocketChannelHandlerFactory;
    }

    public void setWebSocketChannelHandlerFactory(WebSocketChannelHandlerFactory webSocketChannelHandlerFactory) {
        this.webSocketChannelHandlerFactory = webSocketChannelHandlerFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.beanDefinitionRegistry = registry;


        //创建默认的请求处理器
        if (webSocketChannelHandlerFactory == null) {
            WebSocketChannelHandlerFactory factory = new WebSocketChannelHandlerFactory();

            //请求映射处理器
            GenericBeanDefinition requestHandlerMappingBean = new GenericBeanDefinition();
            requestHandlerMappingBean.setBeanClass(WSRequestHandlerMapping.class);
            this.beanDefinitionRegistry.registerBeanDefinition("requestHandlerMapping" , requestHandlerMappingBean );


            //升级请求握手处理器
            GenericBeanDefinition upgradeResolverBean = new GenericBeanDefinition();
            upgradeResolverBean.setBeanClass(UpgradeResolver.class);
            this.beanDefinitionRegistry.registerBeanDefinition("upgradeResolver" , upgradeResolverBean );

            // 存储客户端服务
            GenericBeanDefinition webSocketCacheManagerBean = new GenericBeanDefinition();
            webSocketCacheManagerBean.setBeanClass(WebSocketCacheManager.class);
            this.beanDefinitionRegistry.registerBeanDefinition("webSocketCacheManager" , webSocketCacheManagerBean );

            WSRequestHandlerMapping handlerMapping = this.applicationContext.getBean( WSRequestHandlerMapping.class);
            handlerMapping.setBeanDefinitionRegistry(this.beanDefinitionRegistry);

            UpgradeResolver upgradeResolver = this.applicationContext.getBean(UpgradeResolver.class);
            WebSocketCacheManager webSocketCacheManager = this.applicationContext.getBean(WebSocketCacheManager.class);

            factory.setRequestHandlerMapping(handlerMapping);
            factory.setUpgradeResolver(upgradeResolver);
            factory.setWebSocketCacheManager(webSocketCacheManager);
            webSocketChannelHandlerFactory = factory;
        }

        //启动服务
        startNettyServer();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
