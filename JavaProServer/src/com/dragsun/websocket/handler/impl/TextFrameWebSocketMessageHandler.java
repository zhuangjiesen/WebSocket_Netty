package com.dragsun.websocket.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.utils.LogUtils;
import com.dragsun.websocket.handler.WebSocketMessageHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


/**
 * Created by zhuangjiesen on 2017/8/9.
 */

public class TextFrameWebSocketMessageHandler implements WebSocketMessageHandler {
	


	@Override
	public void onMessage(WebSocketFrame webSocketFrame) {
//		TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) webSocketFrame;
//		String message = textWebSocketFrame.text();
//		if (StringUtils.isNotBlank(message)) {
//			try {
//
//				Channel channel = ctx.channel();
//				String channelId = channel.id().asLongText();
//
//				String[] splitArr = message.split("=");
//				if (splitArr != null && splitArr.length == 2) {
//
//					String trackTagIdStr = splitArr[1];
//					if (NumberUtils.isDigits(trackTagIdStr)) {
//						WebSocketClientCache.registTrackTagId(channelId, NumberUtils.toLong(trackTagIdStr));
//					}
//				} else {
//					WebSocketClientCache.removeTrackTagId(channelId);
//				}
//			} catch (Exception e) {
//				LogUtils.logError(this, e, "analyze message error", message);
//			}
//
//		}
//		LogUtils.logDebug(TextFrameWebSocketMessageHandler.class , "textWebSocketFrame : " + ((TextWebSocketFrame) webSocketFrame).text());

	}
}
