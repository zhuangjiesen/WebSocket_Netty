package com.java.core.netty.common.handler;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class My2InboundChannelHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        System.out.println("---My2InboundChannelHandler ....channelRead.... ");


        super.channelRead(ctx, msg);



    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        System.out.println("---My2InboundChannelHandler ....channelReadComplete.... ");


        super.channelReadComplete(ctx);
    }
}
