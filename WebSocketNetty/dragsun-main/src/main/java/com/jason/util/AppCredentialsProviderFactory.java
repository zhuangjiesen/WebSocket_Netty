package com.jason.util;

import com.google.api.gax.core.CredentialsProvider;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/10
 */
public class AppCredentialsProviderFactory {

    public static CredentialsProvider newCredentialsProvider() {
        return new AppCredentialsProvider();
    }


}
