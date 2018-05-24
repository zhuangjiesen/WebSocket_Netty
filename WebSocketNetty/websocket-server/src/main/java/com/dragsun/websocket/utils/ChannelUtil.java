package com.dragsun.websocket.utils;

import io.netty.channel.ChannelHandlerContext;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public class ChannelUtil {

    public static String getChannelCtxId(ChannelHandlerContext ctx){
        if (ctx == null) {
            return null;
        }
        String id = ctx.channel().id().asLongText();
        return id;
    }

}
