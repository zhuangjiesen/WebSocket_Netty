package com.jason.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Created by zhuangjiesen on 2018/2/17.
 */
@Controller
public class LoginController {


    @RequestMapping(value = {"/login.do" } )
    @ResponseBody
    public String login(){
        return "请登录";
    }





}