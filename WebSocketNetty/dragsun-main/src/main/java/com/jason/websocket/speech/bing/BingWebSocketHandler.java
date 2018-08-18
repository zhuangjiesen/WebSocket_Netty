package com.jason.websocket.speech.bing;

import com.alibaba.fastjson.JSONObject;
import com.dragsun.websocket.client.CloseStatus;
import com.dragsun.websocket.client.WebSocketSession;
import com.dragsun.websocket.handler.websocket.WebSocketHandler;
import com.jason.bing.*;
import com.jason.bing.*;
import com.jason.bing.handler.SpeechPreviewRecognizeEventListener;
import com.jason.bing.util.MessageUtil;
import com.jason.bing.util.WordUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public class BingWebSocketHandler implements WebSocketHandler {

    private Map<String ,SpeechRecognizationClientManager> clientMap = new ConcurrentHashMap<>();




    @Override
    public void beforeConnectionUpgraded(WebSocketSession webSocketSession) throws Exception {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketFrame webSocketFrame) throws Exception {

        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame)webSocketFrame;
            ByteBuf buf = binaryWebSocketFrame.content();
            int re = buf.readableBytes();
            byte[] data = new byte[re];
            buf.getBytes( buf.readerIndex(), data);

            sendData(data , webSocketSession);
        }


    }


    private void sendData(byte[] data , WebSocketSession webSocketSession){
        SpeechRecognizationClientManager client = clientMap.get(webSocketSession.getId());
        if (client == null) {
            client = new SpeechRecognizationClientManager(new SpeechPreviewRecognizeEventListener());

            /*


            client = new SpeechRecognizationClientManager(new AbstractRecognizeEventListener() {
                private long lastMessageTime;

                private SentenceInfo currentSentenceInfo;

                //停顿 300ms * 10000 等于 100纳秒单位
                private int breakMSecond = 200 * 10000;
                //单词数
                private int wordSizeLimit = 0;

                private boolean hasData = false;

                @Override
                public void onSpeechPhrase(RecognizeResponse response) {
                    WordInfo wordInfo = WordUtil.parsePhrase(response.getBodyEntity());
                    if (wordInfo == null) {
                        return ;
                    }
                    hasData = true;
//                    System.out.printf("onSpeechPhrase ----- Got msg: %s%n", JSONObject.toJSON(response));
//                    System.out.println();
                    // 发送识别结果
//                    FrameMessageUtil.sendMessage(webSocketSession , 0 ,  wordInfo.toString());


                    long startTime = wordInfo.getStartTimeNum();
                    if (currentSentenceInfo == null) {
                        return;
                    }

                    List<WordInfo> fragmentList = null;
                    //重置时间，每段蹦字的时间轴都是从0 开始 所以要根据phrase 的起始时间进行计算
                    fragmentList = currentSentenceInfo.getWordInfoList();
                    if (fragmentList != null) {
                        for (WordInfo fragmentItem : fragmentList) {
                            long startTimeOld = fragmentItem.getStartTimeNum();
                            fragmentItem.setStartTimeNum(startTimeOld + startTime);
                            WordUtil.resetTime(fragmentItem);
                        }
                    }

                    int wordSize = currentSentenceInfo.getWordSize();
                    //句子中单词数太多。重新分词
                    if (wordSize > wordSizeLimit) {
                        fragmentList = currentSentenceInfo.getWordInfoList();
                        SentenceInfo newSentenceInfo = new SentenceInfo();
                        if (fragmentList.size() == 1) {
                            newSentenceInfo.addWordInfo(fragmentList.get(0));
                        } else {

                            for (int i = 1 ; i < fragmentList.size() ; i++) {
                                WordInfo lastWord = null;
                                if ((lastWord = newSentenceInfo.getLatestWord()) == null) {
                                    lastWord = fragmentList.get(i - 1);
                                    newSentenceInfo.addWordInfo(lastWord);
                                }
                                WordInfo currentWord = fragmentList.get(i);

                                if (currentWord.getStartTimeNum() == 0) {
                                    //等于新的一句 开始
//                                    FrameMessageUtil.sendMessage(webSocketSession , 2 ,newSentenceInfo.getSentence() );

                                    newSentenceInfo = new SentenceInfo();
                                    newSentenceInfo.addWordInfo(currentWord);
                                } else if ((currentWord.getStartTimeNum() - lastWord.getEndTimeNum()) > breakMSecond) {
                                    //等于新的一句 开始
//                                    FrameMessageUtil.sendMessage(webSocketSession , 2 ,newSentenceInfo.getSentence() );

                                    newSentenceInfo = new SentenceInfo();
                                    newSentenceInfo.addWordInfo(currentWord);
                                } else {
                                    newSentenceInfo.addWordInfo(currentWord);
                                }
                            }
                        }
//                        FrameMessageUtil.sendMessage(webSocketSession , 2 ,newSentenceInfo.getSentence() );
                    }
                    currentSentenceInfo = null;
                }

                @Override
                public void onSpeechFragment(RecognizeResponse response) {
                    if (lastMessageTime == 0) {
                        lastMessageTime = System.currentTimeMillis();
                    }
                    long now = System.currentTimeMillis();
                    long latency = 0;
                    if ((latency = now - lastMessageTime) > 2000) {
                        System.err.println(" 上一条消息与当前接受消息延时 ：" + latency );
                    }
                    lastMessageTime = now;

                    WordInfo wordInfo = WordUtil.parseFragment(response.getBodyEntity());
                    if (wordInfo == null) {
                        return ;
                    }
                    //给前端返回蹦字
                    FrameMessageUtil.sendMessage(webSocketSession , 1 ,  wordInfo.getText());
                    System.out.printf("onSpeechFragment ----- Got msg: %s%n",wordInfo.getText());
                    System.out.println();

                    if (currentSentenceInfo == null) {
                        currentSentenceInfo = new SentenceInfo();
                    }
                    currentSentenceInfo.addWordInfo(wordInfo);
                }


                @Override
                public void onSpeechError(RecognizeResponse response) {
                    System.out.println("response : "+ JSONObject.toJSONString(response));
                }
            });


            * */
            clientMap.put(webSocketSession.getId() , client);
        }
        client.recognizer(data);

    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        System.out.println("========== BingWebSocketHandler ========== handleTransportError  ==========");
        SpeechRecognizationClientManager client = clientMap.remove(webSocketSession.getId());
        if (client != null) {
            client.close();
            client = null;
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        System.out.println("========== BingWebSocketHandler =========== afterConnectionClosed  ==========");
        SpeechRecognizationClientManager client = clientMap.remove(webSocketSession.getId());
        if (client != null) {
            client.close();
            client = null;
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
