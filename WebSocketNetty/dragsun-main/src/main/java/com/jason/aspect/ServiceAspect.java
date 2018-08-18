package com.jason.aspect;

import com.alibaba.fastjson.JSONObject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description: 日志处理
 * @Date: Created in 2018/6/12
 */
@Aspect
@Component
public class ServiceAspect {

    private static Logger logger = LoggerFactory.getLogger(ServiceAspect.class);

    public ServiceAspect() {
        System.out.println();
    }

    @Pointcut("execution(public * com.jason.*.*.*(..))")
    public void webLog(){
        System.out.println();
    }



    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(JoinPoint joinPoint , Object ret) throws Throwable {
        // 处理完请求，返回内容
//        if (ret != null)
//        logger.info(String.format("joinPoint : %s  , result : %s " ,joinPoint.toString() , JSONObject.toJSONString(ret)));
    }





}
