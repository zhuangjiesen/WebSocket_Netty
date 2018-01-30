package com.java.core.netty.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

/**
 * Created by zhuangjiesen on 2017/9/26.
 */
public class CommonByteToMessageDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("...===============CommonByteToMessageDecoder======================...");
        int len = byteBuf.readableBytes();
        byte[] buf = new byte[len];
        byteBuf.readBytes(buf);
        String message = new String(buf , "utf-8");
        System.out.println(" =============== CommonByteToMessageDecoder :  " + message);
    }
}
