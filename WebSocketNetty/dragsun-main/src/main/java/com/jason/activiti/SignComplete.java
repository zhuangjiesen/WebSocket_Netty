package com.jason.activiti;

import java.io.Serializable;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/8/7
 */
public class SignComplete implements Serializable{

    public boolean isComplete() {
        System.out.println("SignComplete - isComplete ... ");
        return true;
    }
}
