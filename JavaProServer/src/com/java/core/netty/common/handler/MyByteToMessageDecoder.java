package com.java.core.netty.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by zhuangjiesen on 2017/9/13.
 */
public class MyByteToMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.isReadable()) {
            int readLength = byteBuf.readableBytes();
            byte[] buf = new byte[readLength];
            byteBuf.readBytes(buf);
            String content = new String(buf , "utf-8");
            System.out.println("  MyByteToMessageDecoder .... content : " + content);
            list.add(content);
        }

        System.out.println("  MyByteToMessageDecoder .... decode ");


    }
}
