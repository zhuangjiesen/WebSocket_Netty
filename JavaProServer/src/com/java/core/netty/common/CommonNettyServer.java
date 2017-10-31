package com.java.core.netty.common;

import com.java.core.netty.common.handler.My1InboundChannelHandler;
import com.java.core.netty.common.handler.My2InboundChannelHandler;
import com.java.core.netty.common.handler.MyByteToMessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * netty websocket 服务器
 *
 * Created by zhuangjiesen on 2017/8/8.
 */
public class CommonNettyServer {


    /** 端口号 **/
    private int port;



    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CommonNettyServer() {
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

                            p.addLast(new MyByteToMessageDecoder());
                            p.addLast(new My1InboundChannelHandler());
                            p.addLast(new My2InboundChannelHandler());

                        }
                    });

                    ChannelFuture f = bootstrap.bind(port).sync();


                    if (f.isSuccess()) {
                        System.out.println(" WebSocketNettyServer start successfully ....");
                    }
                    f.channel().closeFuture().sync();

                    System.out.println(" WebSocketNettyServer start successfully ....");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(" WebSocketNettyServer throw exception : " + e.getMessage());
                } finally {
                    System.out.println(" WebSocketNettyServer shutdownGracefully ....");
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



}
