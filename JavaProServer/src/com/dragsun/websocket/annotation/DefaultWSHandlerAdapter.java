package com.dragsun.websocket.annotation;

import java.lang.annotation.*;

/**
 *
 * 用来标识默认请求处理器
 * Created by zhuangjiesen on 2017/11/3.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultWSHandlerAdapter {
}
