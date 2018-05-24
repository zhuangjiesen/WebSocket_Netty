package com.jason.websocket.speech.google;

import com.dragsun.websocket.client.CloseStatus;
import com.dragsun.websocket.client.WebSocketSession;
import com.dragsun.websocket.handler.websocket.WebSocketHandler;
import com.jason.util.GoogleSpeechToTextUtil;
import com.jason.util.WaveHeader;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public class GoogleWebSocketHandler implements WebSocketHandler {

    private static File file = null;

    private static String filePath = null;


    @Override
    public void beforeConnectionUpgraded(WebSocketSession webSocketSession) throws Exception {
        System.out.println("==========  BingWebSocketHandler ========== beforeConnectionUpgraded ==========");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        System.out.println("==========  BingWebSocketHandler ====== afterConnectionEstablished ==========");
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketFrame webSocketFrame) throws Exception {
        System.out.println("========== BingWebSocketHandler =========== handleMessage  ==========");


        /*
        *  生成音频文件
        *

 * */



        if (webSocketFrame instanceof TextWebSocketFrame) {
            String path = "/Users/zhuangjiesen/netease/projects/dev/tmp/";
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)webSocketFrame;
            String text = textWebSocketFrame.text();
            if ("start".equals(text)) {
                String fileName = "record-".concat(String.valueOf(System.currentTimeMillis()));
                filePath = path.concat(fileName);
                file = new File(filePath);
                file.createNewFile();
            }
            if ("stop".equals(text)) {
                Path mPath = Paths.get(filePath);
                byte[] data = Files.readAllBytes(mPath);
                int PCMSize = data.length;
                byte[] wavHeader = WaveHeader.init(PCMSize , 16000 );
                String fileName = "wav-".concat(String.valueOf(System.currentTimeMillis())).concat(".wav");
                File wavFile = new File(path.concat(fileName));
                wavFile.createNewFile();
                mPath = Paths.get(wavFile.getPath());
                Files.write(mPath ,wavHeader );

                try {
                    // 打开一个随机访问文件流，按读写方式
                    RandomAccessFile randomFile = new RandomAccessFile(wavFile.getPath(), "rw");
                    // 文件长度，字节数
                    long fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
                    randomFile.write(data);
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long start = System.currentTimeMillis();
//                GoogleSpeechToTextUtil.streamingRecognizeData(data);
//                GoogleSpeechToTextUtil.recognizedLocalData(data);
//                GoogleSpeechToTextUtil.asyncRecognizeWordsData(data);

                GoogleSpeechToTextUtil.asyncRecognizeWordsData(data , webSocketSession);
                long time = System.currentTimeMillis() - start;
                System.out.println(" time : " + time);
                webSocketSession.sendMessage(new TextWebSocketFrame("------ Recognize Time : " + String.valueOf(time) + " --------- "));
            }
            System.out.println(" text : " + text );


            webSocketSession.sendMessage(new TextWebSocketFrame("------ recieve..."));

        }
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)webSocketFrame;
            ByteBuf buf = binaryWebSocketFrame.content();
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            int len = req.length;


            if (file != null) {
                try {
                    // 打开一个随机访问文件流，按读写方式
                    RandomAccessFile randomFile = new RandomAccessFile(file.getPath(), "rw");
                    // 文件长度，字节数
                    long fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
                    randomFile.write(req);
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println(" len : " + len );
        }

        /*实时语音识别
        *



        if (webSocketFrame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame)webSocketFrame;
            String text = textWebSocketFrame.text();

            System.out.println(" text : " + text );
        }
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)webSocketFrame;
            ByteBuf buf = binaryWebSocketFrame.content();

            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            GoogleSpeechToTextUtil.streamingRecognizeData(req);
        }


        *
        * */



    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        System.out.println("========== BingWebSocketHandler ========== handleTransportError  ==========");

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        System.out.println("========== BingWebSocketHandler =========== afterConnectionClosed  ==========");

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
