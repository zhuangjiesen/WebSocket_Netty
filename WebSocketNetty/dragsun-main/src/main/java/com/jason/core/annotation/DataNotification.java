package com.jason.core.annotation;

import java.lang.annotation.*;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:数据通知注解
 * @Date: Created in 2018/7/18
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataNotification {
}
