package com.dragsun.websocket.handler.netty;

import com.dragsun.websocket.utils.ChannelUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/15
 */
public abstract class AbstractInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String id = ChannelUtil.getChannelCtxId(ctx);
        this.doChannelRead(id , ctx , msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        String id = ChannelUtil.getChannelCtxId(ctx);
        this.doExceptionCaught(id , ctx , cause);
    }


    public abstract void doChannelRead(String channelId ,ChannelHandlerContext ctx, Object msg) throws Exception;
    public abstract void doExceptionCaught(String channelId ,ChannelHandlerContext ctx, Throwable cause) throws Exception;


}
