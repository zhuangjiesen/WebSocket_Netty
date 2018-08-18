package com.jason.service;

import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/10
 */
@Service
public class TestService {


    public void doSer1(){
        System.out.println("hello 1 ...");

//        this.doSer2();
        TestService testService = (TestService) AopContext.currentProxy();
        testService.doSer2();
    }


    public void doSer2(){
        System.out.println("hello 2 ...");
    }



}
