package com.jason.controller;


import com.jason.bing.*;
import com.jason.bing.util.WordUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhuangjiesen on 2018/2/17.
 */
@RestController
public class TestController {

    @RequestMapping("test")
    public String test(){
        return "test";
    }


    /*
    * 创建新会议
    *
    * */
    @RequestMapping("saveMeeting.do")
    public Map<String, Object> saveMeeting(String meetingName){
        Map<String, Object> resp = new HashMap<>();


        resp.put("success" , "1");
        return resp;
    }



    /*
    * 推送会议消息
    *
    * */
    @RequestMapping("pushMeetingMessage.do")
    public Map<String, Object> saveMeeting(String meetingId , String message){
        Map<String, Object> resp = new HashMap<>();
        resp.put("success" , "1");
        return resp;
    }



    //处理文件上传
    @RequestMapping(value="/testuploadimg", method = RequestMethod.POST)
    public @ResponseBody Map<String , Object> uploadImg(@RequestParam("audiofile") MultipartFile file,
                     HttpServletRequest request) throws Exception {
        Map<String , Object> result = new HashMap<>();

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        result.put("success" , true);
        // wav 文件 判断
        if (!fileName.endsWith(".wav")) {
            result.put("success" , false);
            result.put("message" , "文件类型错误");
            return result;
        }

        //文件数据
        byte[] audioData = file.getBytes();
        BingSpeechWebSocketClient client = new BingSpeechWebSocketClient();
        RecognizerConfig recognizerConfig = RecognizerConfig.getDefaultRecognizerConfig();
//        recognizerConfig.setRecognitionMode(SpeechEventConstant.MODE_CONVERSATION);
        // 用作同步方法
        final CountDownLatch countor = new CountDownLatch(1);
        client.createRecognizer(recognizerConfig, SpeechEventConstant.SUBSCRIPTION_KEY , new AbstractRecognizeEventListener() {
            private List<WordInfo> phraseList = new ArrayList<>();
            //停顿 300ms * 10000 等于 100纳秒单位
            private int breakMSecond = 200 * 10000;
            //单词数
            private int wordSizeLimit = 0;

            private List<SentenceInfo> sentenceInfoList = new ArrayList<SentenceInfo>();
            private SentenceInfo currentSentenceInfo;

            @Override
            public void onSpeechPhrase(RecognizeResponse response) {
                WordInfo wordInfo = WordUtil.parsePhrase(response.getBodyEntity());
                if (wordInfo == null) {
                    return;
                }
                phraseList.add(wordInfo);
                int startTime = wordInfo.getStartTimeNum();
                if (currentSentenceInfo == null) {
                    return;
                }


                //重置时间，每段的时间轴都是从0 开始
                List<WordInfo> fragmentList = currentSentenceInfo.getWordInfoList();
                if (fragmentList != null) {
                    for (WordInfo fragmentItem : fragmentList) {
                        int startTimeOld = fragmentItem.getStartTimeNum();
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
                            if ((lastWord = newSentenceInfo.getLatestWord())== null) {
                                lastWord = fragmentList.get(i - 1);
                                newSentenceInfo.addWordInfo(lastWord);
                            }
                            WordInfo currentWord = fragmentList.get(i);

                            if (currentWord.getStartTimeNum() == 0) {
                                //等于新的一句 开始
                                sentenceInfoList.add(newSentenceInfo);
                                newSentenceInfo = new SentenceInfo();
                                newSentenceInfo.addWordInfo(currentWord);
                            } else if ((currentWord.getStartTimeNum() - lastWord.getEndTimeNum()) > breakMSecond) {
                                //等于新的一句 开始
                                sentenceInfoList.add(newSentenceInfo);
                                newSentenceInfo = new SentenceInfo();
                                newSentenceInfo.addWordInfo(currentWord);
                            } else {
                                newSentenceInfo.addWordInfo(currentWord);
                            }
                        }
                    }
                    sentenceInfoList.add(newSentenceInfo);
                } else {
                    sentenceInfoList.add(currentSentenceInfo);
                }
                currentSentenceInfo = null;
            }

            @Override
            public void onSpeechFragment(RecognizeResponse response) {
                WordInfo wordInfo = WordUtil.parseFragment(response.getBodyEntity());
                if (wordInfo == null) {
                    return ;
                }
                if (currentSentenceInfo == null) {
                    currentSentenceInfo = new SentenceInfo();
                }
                currentSentenceInfo.addWordInfo(wordInfo);
            }

            @Override
            public void onTurnEnd(RecognizeResponse response) {
                System.out.println("phrase : ---------------------");
                //bing翻译结果
                List<String> plainList = new ArrayList<String>();
                // 翻译后人工断句的结果
                List<String> senList = new ArrayList<String>();
                for (WordInfo item : phraseList) {
                    System.out.println(item);
                    plainList.add(item.toString());
                }

                System.out.println("=============================");

                System.out.println("fragment : ----------------------");

                for (SentenceInfo item : sentenceInfoList) {
                    List<WordInfo> fragmentList = item.getWordInfoList();
                    for (WordInfo wordItem : fragmentList) {
                        System.out.println(wordItem.toString());
                    }
                }
                System.out.println("=============================");

                System.out.println("sentence : ----------------------");
                for (SentenceInfo item : sentenceInfoList) {
                    System.out.println(item.getSentence());
                    senList.add(item.getSentence());
                }

                result.put("phraseList" , plainList);
                result.put("sentenceInfoList" , senList);
                //同步输出结果
                countor.countDown();
            }
        });
        //开始识别
        client.recognizerAudioData(file.getBytes());

        try {
            countor.await();
            client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //返回json
        return result;
    }


}