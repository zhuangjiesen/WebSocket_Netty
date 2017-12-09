package com.dragsun.websocket.annotation;

import java.lang.annotation.*;

/**
 * Created by zhuangjiesen on 2017/11/15.
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WSMessageType {

    String value();

}
