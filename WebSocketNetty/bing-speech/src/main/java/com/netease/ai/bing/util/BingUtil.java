package com.netease.ai.bing.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/21
 */
public class BingUtil {


    public static String getTimestamp() {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss:SSSXXX";
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        String timestamp = null;
        timestamp = dateFormat.format(new Date());
        return timestamp;
    }
}
