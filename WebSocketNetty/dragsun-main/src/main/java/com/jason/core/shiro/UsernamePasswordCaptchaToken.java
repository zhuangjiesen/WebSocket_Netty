package com.jason.core.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/7/27
 */
public class UsernamePasswordCaptchaToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 1L;

    /**  验证码*/
    private String captcha;

    /**  用户类型 */
    private String userType;

    public UsernamePasswordCaptchaToken(String username, char[] password, boolean rememberMe, String host, String userType, String captcha) {
        super(username, password, rememberMe, host);
        this.userType = userType;
        this.captcha = captcha;
    }


    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}