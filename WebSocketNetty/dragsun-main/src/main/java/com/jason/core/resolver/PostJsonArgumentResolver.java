package com.jason.core.resolver;

import com.alibaba.fastjson.JSONObject;
import com.jason.core.annotation.PostJsonParam;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/6/25
 */
public class PostJsonArgumentResolver extends AbstractMessageConverterMethodProcessor implements Ordered {


    ThreadLocal<JSONObject> parameterJson = new ThreadLocal<>();

    public PostJsonArgumentResolver(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    public PostJsonArgumentResolver(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager contentNegotiationManager) {
        super(converters, contentNegotiationManager);
    }

    public PostJsonArgumentResolver(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, manager, requestResponseBodyAdvice);
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        String name = methodParameter.getParameterName();
        PostJsonParam postJsonParam = methodParameter.getMethod().getAnnotation(PostJsonParam.class);
        boolean support = postJsonParam != null;
        return false;
    }


    /**
     * 判断方法结尾Z
     * @author zhuangjiesen
     * @date 2018/6/26 上午9:08
     * @param
     * @return
     */
    public boolean isEndOfMethod (MethodParameter currentMethodParameter, int totalCount) {
        if (totalCount == (currentMethodParameter.getParameterIndex() + 1)) {
            return true;
        }

        return false;
    }


    @Override
    public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        JSONObject content = null;
        if (parameterJson.get() == null) {
            ServletServerHttpRequest inputMessage = this.createInputMessage(nativeWebRequest);
            InputStream ins = inputMessage.getBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            StringBuilder contentSb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                contentSb.append(line);
                contentSb.append("\r\n");
            }
            JSONObject json = JSONObject.parseObject(contentSb.toString());
            content = json;
            parameterJson.set(content);
        } else {
            content = parameterJson.get();
        }
        //比较方法一致
        int argCount = methodParameter.getMethod().getParameterCount();
        String name = methodParameter.getParameterName();

        if (isEndOfMethod(methodParameter , argCount)) {
            parameterJson.remove();
        }
        if (content != null) {
            Object obj = content.getObject(name , methodParameter.getParameterType());
            return obj;
        }
        return null;
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return false;
    }

    @Override
    public void handleReturnValue(@Nullable Object o, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {

    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
