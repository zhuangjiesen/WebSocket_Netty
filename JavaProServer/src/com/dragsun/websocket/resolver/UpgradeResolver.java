package com.dragsun.websocket.resolver;

import com.dragsun.websocket.constant.WebSocketConstant;
import com.sun.javafx.binding.StringFormatter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.util.Set;

/**
 *
 *
 * Websocket 的 upgrade 处理
 * Created by zhuangjiesen on 2017/9/13.
 */
public class UpgradeResolver {


    private int frameLength;



    public WebSocketServerHandshaker handleRequest(ChannelHandlerContext ctx , FullHttpRequest request ){
        boolean result = false;
        //处理升级请求
        result = handleUpgradeRequest(ctx, request);
        if (result) {
            //拦截过滤 -> 可能对cookie 或者 作权限验证
            result = doFilter(ctx, request);
            if (result) {
                WebSocketServerHandshaker handshaker = null;
                //握手成功
                if ((handshaker = doHandshake(ctx, request )) != null) {
                    return handshaker;
                }
            }
        }
        return null;
    }




    /*
    *
    * 处理异常请求
    *
    * */
    public void handleRequestError(ChannelHandlerContext ctx , FullHttpRequest request , Throwable throwable){
        ByteBuf content = null;
        DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY);

        ByteBuf buf = Unpooled.copiedBuffer(throwable.getMessage() ,
                CharsetUtil.UTF_8);
        defaultFullHttpResponse.content().writeBytes(buf);
        buf.release();
        ChannelFuture f = ctx.channel().writeAndFlush(defaultFullHttpResponse);
    }



    /*
    * 处理请求
    * */
    public boolean handleUpgradeRequest(ChannelHandlerContext ctx , FullHttpRequest request){
        HttpHeaders httpHeaders = request.headers();
        //判断请求头
        if (!request.decoderResult().isSuccess()
                || (!WebSocketConstant.WEBSOCKET.equals(httpHeaders.get(WebSocketConstant.Upgrade).toString().toLowerCase()))) {


            DefaultFullHttpResponse defaultFullHttpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);

            // 返回应答给客户端
            if (defaultFullHttpResponse.status().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.status().toString(),
                        CharsetUtil.UTF_8);
                defaultFullHttpResponse.content().writeBytes(buf);
                buf.release();
            }
            // 如果是非Keep-Alive，关闭连接
            ChannelFuture f = ctx.channel().writeAndFlush(defaultFullHttpResponse);

            boolean isKeepAlive = false;

            if ((!isKeepAlive) || defaultFullHttpResponse.status().code() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }

            return false;
        }
        return true;
    }



    /*
    * 拦截请求
    * 可以进行权限验证
    *
    * */
    public boolean doFilter(ChannelHandlerContext ctx , FullHttpRequest request){
//      HttpHeaders httpHeaders = request.headers();
//      String cookie = httpHeaders.get("Cookie");
      return true;
    }




    /*
    * 握手连接
    *
    *
    * */
    public WebSocketServerHandshaker doHandshake(ChannelHandlerContext ctx , FullHttpRequest request ){
        HttpHeaders httpHeaders = request.headers();
        String protocols = httpHeaders.get( WebSocketConstant.SEC_WEBSOCKET_PROTOCOL);
//        "Sec-WebSocket-Protocol" -> "location.do, default.do"
        String host = httpHeaders.get("Host").toString();
        String uri = request.uri();
        String webAddress = StringFormatter.format(WebSocketConstant.DEFAULT_WEBSOCKET_ADDRESS_FORMAT , host).getValueSafe() + uri;

        //设置最大帧长度，保证安全
        if (frameLength == 0) {
            frameLength = 10 * 1024 * 1024;
        }
        WebSocketServerHandshakerFactory wsFactory = wsFactory = new WebSocketServerHandshakerFactory(
                webAddress , protocols , true , frameLength );
//        if (wsFactory == null) {
//            wsFactory = new WebSocketServerHandshakerFactory(
//                    webAddress , uri, true , frameLength );
//        }
        WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            //版本不兼容
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
        return handshaker;
    }


}
