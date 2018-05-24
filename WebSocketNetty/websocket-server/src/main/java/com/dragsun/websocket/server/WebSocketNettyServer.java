package com.dragsun.websocket.server;


import com.dragsun.websocket.handler.websocket.WebSocketHandler;
import com.dragsun.websocket.ssl.SslContextFactory;
import com.dragsun.websocket.utils.LogUtils;
import com.dragsun.websocket.handler.WebSocketChannelHandlerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * netty websocket 服务器
 *
 * Created by zhuangjiesen on 2017/8/8.
 */
public class WebSocketNettyServer implements InitializingBean , ApplicationContextAware , BeanDefinitionRegistryPostProcessor{

    private Configuration configuration;

    private WebSocketChannelHandlerFactory webSocketChannelHandlerFactory = new WebSocketChannelHandlerFactory();
    /** 端口号 **/
    private int port;
    /** worker 线程数**/
    private int workerCount;
    private int backlog = 1024;
    private boolean tcpNodelay = true;
    private boolean keepalive = true;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private ApplicationContext applicationContext ;

    public WebSocketNettyServer() {
        super();
        // TODO Auto-generated constructor stub
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void startNettyServer() {
        SSLContext sslCtx = SslContextFactory.getServerContext();

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup(workerCount);
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, backlog); //连接数
                    bootstrap.option(ChannelOption.TCP_NODELAY, tcpNodelay);  //不延迟，消息立即发送
//		            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000);  //超时时间
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, keepalive); //长连接
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel)
                                throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();

                            //HttpServerCodec: 针对http协议进行编解码
                            p.addLast("http-codec", new HttpServerCodec());
                            /**
                             * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
                             * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
                             */
                            p.addLast("aggregator", new HttpObjectAggregator(65536));
                            //ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
                            p.addLast("http-chunked", new ChunkedWriteHandler());
                            //请求处理
                            p.addLast("inboundHandler", webSocketChannelHandlerFactory.newWSInboundHandler());
                            //关闭处理
                            p.addLast("outboundHandler", webSocketChannelHandlerFactory.newWSOutboundHandler());

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
        //启动服务
        webSocketChannelHandlerFactory.setConfiguration(configuration);
        startNettyServer();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }


    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public boolean isTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }

    public boolean isKeepalive() {
        return keepalive;
    }

    public void setKeepalive(boolean keepalive) {
        this.keepalive = keepalive;
    }

}
