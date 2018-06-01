package com.jason.bing.util;

import com.alibaba.fastjson.JSONObject;
import com.jason.bing.*;
import com.jason.bing.handler.AudioFileRecognizationHandler;
import com.jason.bing.handler.LongAudioHandler;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 通用工具类
 * @Date: Created in 2018/5/21
 */
public class BingUtil {

    private static final Logger LOGGER = Logger.getLogger(BingUtil.class);

    //任务队列
//    private static ExecutorService executor = Executors.newScheduledThreadPool(3);


    /*
     *
     * 必应识别的sn码 区分bing识别任务
     * @author zhuangjiesen
     * @date 2018/5/31 上午9:35
     * @param
     * @return
     */
    public static String getSNUUID() {
        String uuid = String.format(SpeechEventConstant.BING_SN_PREFIX , UUID.randomUUID().toString());
        return uuid;
    }




    public static String getTimestamp() {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSXXX";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String timestamp = null;
        timestamp = dateFormat.format(new Date());
        return timestamp;
    }



    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }


    /*
     *
     * 计算需要切割的文件数量
     * @author zhuangjiesen
     * @date 2018/5/31 上午9:50
     * @param
     * @return
     */
    public static int getSmallAudioFileCount(long duration ,  long perAudioTime) {
        int count = 0;
        count = (int) (duration / perAudioTime);
        return count;
    }



    /*
     *
     * 是否支持必应识别
     * @author zhuangjiesen
     * @date 2018/5/30 上午10:59
     * @param
     * @return
     */
    public static boolean supportsBing(String lang){
        if (Locale.ENGLISH.toString().equals(lang) && SpeechEventConstant.BING_ENABLE && isUserEnable()) {
            LOGGER.info(" supportsBing is true !.");
            return true;
        }
        return false;
    }


    public static boolean isUserEnable(){
        return true;
    }



}
