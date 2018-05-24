package com.jason.websocket.speech.bing;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:计时器
 * @Date: Created in 2018/5/23
 */
public class TimeCountor {

    /** 连接限制 **/
    public static final long LIMIT_TIME = 30 * 1000L;



    public long start;
    public long end;
    public long dis;

    public TimeCountor() {
        this.start = System.currentTimeMillis();

    }

    public void countTime() {
        this.end = System.currentTimeMillis();
    }


    public long getDis() {
        dis = this.end - this.start;
        return dis;
    }

    public boolean isUp() {
        return (getDis() - LIMIT_TIME) > 0;
    }
}
