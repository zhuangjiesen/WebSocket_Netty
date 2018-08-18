package com.jason.core.annotation;


import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 解决post 请求参数问题
 * @Date: Created in 2018/6/11
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostJsonParam {
}
