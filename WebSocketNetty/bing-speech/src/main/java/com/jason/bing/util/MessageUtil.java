package com.jason.bing.util;

import com.alibaba.fastjson.JSONObject;
import com.jason.bing.SpeechEventConstant;
import com.jason.bing.RecognizeResponse;

import java.util.UUID;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 消息工具类
 * @Date: Created in 2018/5/21
 */
public class MessageUtil {


    /*
     * speech.config websocket连接完成后发送消息
     * @author zhuangjiesen
     * @date 2018/5/20 下午4:18
     * @param
     * @return
     */
    public static String getSpeechConfigMessage(String requestId) {
        StringBuilder message  = new StringBuilder();
        message.append(SpeechEventConstant.PATH);
        message.append(": ");
        message.append(SpeechEventConstant.SPEECH_CONFIG);
        message.append(SpeechEventConstant.LINE_SEP);
        message.append(SpeechEventConstant.X_REQUESTID);
        message.append(": ");
        message.append(requestId);
        message.append(SpeechEventConstant.LINE_SEP);
        message.append("x-timestamp: ");
        message.append(BingUtil.getTimestamp());
        message.append(SpeechEventConstant.LINE_SEP);
        message.append(SpeechEventConstant.LINE_SEP);
        String body = "{\"context\":{\"system\":{\"version\":\"1.0.00000\"},\"os\":{\"platform\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36\",\"name\":\"Browser\",\"version\":null},\"device\":{\"manufacturer\":\"SpeechSample\",\"model\":\"SpeechSample\",\"version\":\"1.0.00000\"}}}";
        message.append(body);
        return message.toString();
    }


    public static RecognizeResponse getRecognizeResponse(String message) {
        String[] result = message.split(SpeechEventConstant.LINE_SEP);
        RecognizeResponse response = new RecognizeResponse();

        int headerpos = 0;
        String body = null;
        for (int i = 0 ; i < result.length ; i ++) {
            String item = result[i];
            String[] headerItem = item.split(":");
            if (headerItem.length == 2) {
                String name = headerItem[0];
                String value = headerItem[1];
                response.addHeaders(name , value);
            }
            if ("".equals(item)) {
                // 加上 \r\n 4个字节 空行去掉
                body = message.substring(headerpos + 2);
                break;
            }
            // 加上 \r\n 4个字节
            headerpos += item.length() + 2;
        }
        response.setBody(body);
        response.setBodyEntity(JSONObject.parseObject(body));
        return response;
    }




    public static byte[] getAudioHeader(String requestId){
        StringBuilder audioReqHeader = new StringBuilder();
        audioReqHeader.append("path: audio");
        audioReqHeader.append(SpeechEventConstant.LINE_SEP);
//            audioReqHeader.append("Content-Type: audio/x-wav");
//            audioReqHeader.append(lineSep);
        audioReqHeader.append("x-requestid: ");
        audioReqHeader.append(requestId);
        audioReqHeader.append(SpeechEventConstant.LINE_SEP);
        audioReqHeader.append("x-timestamp: ");
        audioReqHeader.append(BingUtil.getTimestamp());
        audioReqHeader.append(SpeechEventConstant.LINE_SEP);
        audioReqHeader.append(SpeechEventConstant.LINE_SEP);
        return audioReqHeader.toString().getBytes();
    }




    public static byte[] getAudioMessage(byte[] header , byte[] content) {
        byte[] headerLen = getBinaryHeaderLen((short) header.length);
        byte[] data = new byte[2 + header.length + content.length];
        data[0] = headerLen[0];
        data[1] = headerLen[1];
        int pos = 2;
        System.arraycopy(header , 0 , data , pos , header.length);
        pos = pos + header.length;
        System.arraycopy(content , 0 , data , pos , content.length);
        return data;
    }



    /*
     *
     * 获取二进制(binary)消息头长度
     * @author zhuangjiesen
     * @date 2018/5/20 下午2:55
     * @param
     * @return
     */
    public static byte[] getBinaryHeaderLen(short len) {
        byte[] headerLen = new byte[2];
        headerLen[0] = (byte) (len >> 8);
//        byte right = s & 0x00ff;
        //00000001 00101100
        //1111111
        //len的值  xxxxxxxx(left) xxxxxxxx(right)
        // 16位的short 通过位运算 存在2个byte 中
        //00000000 11111111  (255)
        headerLen[1] = (byte) (len & 255);
        return headerLen;
    }






    /*
     * speech.telemetry websocket 消息发送完成后发送消息
     * @author zhuangjiesen
     * @date 2018/5/20 下午4:18
     * @param
     * @return
     */
    public static String getSpeechTelemetryHeader(String requestId) {
        StringBuilder message  = new StringBuilder();
        message.append(SpeechEventConstant.PATH);
        message.append(": ");
        message.append(SpeechEventConstant.SPEECH_TELEMETRY);
        message.append(SpeechEventConstant.LINE_SEP);
        message.append(SpeechEventConstant.X_REQUESTID);
        message.append(": ");
        message.append(requestId);
        message.append(SpeechEventConstant.LINE_SEP);
        message.append("x-timestamp: ");
        message.append(BingUtil.getTimestamp());
        message.append(SpeechEventConstant.LINE_SEP);
        message.append("Content-Type: application/json");
        message.append(SpeechEventConstant.LINE_SEP);
        message.append(SpeechEventConstant.LINE_SEP);
        return message.toString();
    }




    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }
}
