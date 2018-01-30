package com.java.core.netty.common;

import com.java.core.netty.common.handler.CommonByteToMessageDecoder;
import com.java.core.netty.common.handler.CommonMessageToByteEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhuangjiesen on 2017/12/25.
 */
public class CommonNettyServer {


    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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
                            p.addLast("commonMessageToByteEncoder", new CommonMessageToByteEncoder());
                            p.addLast("commonByteToMessageEncoder", new CommonByteToMessageDecoder());
                        }
                    });

                    ChannelFuture f = bootstrap.bind(18888);
                    f.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {

                        }
                    });
                    System.out.println(" class : " + f.channel().getClass().getName());
                    f.sync();

                    if (f.isSuccess()) {
                        System.out.println(" CommonNettyServer start successfully ....");
                    }
                    f.channel().closeFuture().sync();

                    System.out.println(" CommonNettyServer start successfully ....");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(" CommonNettyServer throw exception : " + e.getMessage());
                } finally {
                    System.out.println(" CommonNettyServer shutdownGracefully ....");
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        });


    }




}
