package com.jason.util;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/10
 */
public class AppCredentialsProvider implements CredentialsProvider {

    private static final String jsonPath = "/Users/zhuangjiesen/netease/projects/dev/nmtp/google/google/xxxxxx.json";


    @Override
    public Credentials getCredentials() throws IOException {
        File file = new File(jsonPath);
        FileInputStream fins = new FileInputStream(file);
        GoogleCredentials credentials = GoogleCredentials.fromStream(fins);
        return credentials;
    }
}
