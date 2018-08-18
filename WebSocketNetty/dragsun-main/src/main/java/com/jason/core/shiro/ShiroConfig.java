package com.jason.core.shiro;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * shiro配置引入
 * @Date: Created in 2018/7/27
 */
@Configuration
//@ImportResource(locations={"classpath:shiro/shiroConfig.xml"})
public class ShiroConfig implements CommandLineRunner {


    @Override
    public void run(String... strings) throws Exception {
        System.out.println("ShiroConfig.....");
    }



}
