package com.dragsun.websocket.constant;


import com.alibaba.dubbo.common.utils.StringUtils;

/**
 * Created by zhuangjiesen on 2017/11/14.
 */
public enum WebSocketMessageType {

    //定位消息
    LOCATION_MESSAGE("1") ,
    // 人员统计消息
    PERSON_STATISTICS_MESSAGE("2") ,
    //
    JYCALLINGSTATUS_MESSAGE("3") ,

    ;
    private String type ;

    private WebSocketMessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


    public static boolean hasMessageType(String type){
        if (StringUtils.isEmpty(type)) {
            return false;
        }
        WebSocketMessageType[] values = WebSocketMessageType.values();
        for (WebSocketMessageType typeItem : values) {
            if (typeItem.getType().equals(type)){
                return true;
            }
        }
        return false;
    }


}
