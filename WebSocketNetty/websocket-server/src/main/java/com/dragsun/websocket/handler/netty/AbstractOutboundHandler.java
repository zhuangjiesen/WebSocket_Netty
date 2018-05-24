package com.dragsun.websocket.handler.netty;

import com.dragsun.websocket.utils.ChannelUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/15
 */
public abstract class AbstractOutboundHandler extends ChannelOutboundHandlerAdapter {



    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        String id = ChannelUtil.getChannelCtxId(ctx);
        this.doDisconnect(id , ctx , promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        String id = ChannelUtil.getChannelCtxId(ctx);
        this.doClose(id , ctx , promise);
        super.close(ctx, promise);
    }


    public abstract void doDisconnect(String channelId ,ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;
    public abstract void doClose(String channelId ,ChannelHandlerContext ctx, ChannelPromise promise) throws Exception;
}
