package com.dragsun.websocket.utils;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuangjiesen on 2017/9/14.
 */
public class MessageUtils {


    public static String getHttpGetUri(String uri){
        int index = -1;
        if (uri != null && uri.length() > 0 && ((index = uri.indexOf("?")) > - 1)) {
            String requestUri = uri.substring(0 , index );
            return requestUri;
        }
        return uri;
    }

    public static Map<String , Object> getHttpGetParams(String uri){
        int index = -1;
        if (uri != null && uri.length() > 0 && ((index = uri.indexOf("?")) > - 1)) {
            String requestUri = uri.substring(index + 1);
            String[] reqs = requestUri.split("&");
            if (reqs != null && reqs.length > 0) {
                Map<String , Object> params = new HashMap<>();
                //name value 交替
                for (String req : reqs) {
                    String[] nameAndValue = req.split("=");
                    if (nameAndValue != null && nameAndValue.length == 2) {
                        String name = nameAndValue[0];
                        String value = nameAndValue[1];
                        params.put(name , value);
                    }
                }
                return params;
            }
        }
        return null;
    }


    /**
     * 判断是否含有特殊字符
     *
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }



}
