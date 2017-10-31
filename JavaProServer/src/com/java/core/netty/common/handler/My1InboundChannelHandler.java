package com.java.core.netty.common.handler;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class My1InboundChannelHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("---My1InboundChannelHandler ....channelRead.... ");

        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("---My1InboundChannelHandler ....channelReadComplete.... ");
    }
}
