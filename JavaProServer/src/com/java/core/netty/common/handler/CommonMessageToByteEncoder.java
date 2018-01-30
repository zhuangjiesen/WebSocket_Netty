package com.java.core.netty.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by zhuangjiesen on 2017/9/26.
 */
public class CommonMessageToByteEncoder extends MessageToByteEncoder {


    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        System.out.println("...=============CommonMessageToByteEncoder========================...");
        String message = (String)msg;
        out.writeBytes(message.getBytes("utf-8"));

    }
}
