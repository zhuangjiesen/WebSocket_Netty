package com.dragsun.websocket.client;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @param
 * @Author: zhuangjiesen
 * @Description:
 * @Date: Created in 2018/5/12
 */
public class WebSocketExtension {
    private final String name;
    private final Map<String, String> parameters;

    public WebSocketExtension(String name) {
        this(name, (Map)null);
    }

    public WebSocketExtension(String name, Map<String, String> parameters) {
        Assert.hasLength(name, "Extension name must not be empty");
        this.name = name;
        if(!CollectionUtils.isEmpty(parameters)) {
            LinkedCaseInsensitiveMap map = new LinkedCaseInsensitiveMap(parameters.size(), Locale.ENGLISH);
            map.putAll(parameters);
            this.parameters = Collections.unmodifiableMap(map);
        } else {
            this.parameters = Collections.emptyMap();
        }

    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public boolean equals(Object other) {
        if(this == other) {
            return true;
        } else if(other != null && this.getClass() == other.getClass()) {
            WebSocketExtension otherExt = (WebSocketExtension)other;
            return this.name.equals(otherExt.name) && this.parameters.equals(otherExt.parameters);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.name.hashCode() * 31 + this.parameters.hashCode();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.name);
        Iterator var2 = this.parameters.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry entry = (Map.Entry)var2.next();
            str.append(';');
            str.append((String)entry.getKey());
            str.append('=');
            str.append((String)entry.getValue());
        }

        return str.toString();
    }

    public static List<WebSocketExtension> parseExtensions(String extensions) {
        if(!StringUtils.hasText(extensions)) {
            return Collections.emptyList();
        } else {
            String[] tokens = StringUtils.tokenizeToStringArray(extensions, ",");
            ArrayList result = new ArrayList(tokens.length);
            String[] var3 = tokens;
            int var4 = tokens.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String token = var3[var5];
                result.add(parseExtension(token));
            }

            return result;
        }
    }

    private static WebSocketExtension parseExtension(String extension) {
        if(extension.contains(",")) {
            throw new IllegalArgumentException("Expected single extension value: [" + extension + "]");
        } else {
            String[] parts = StringUtils.tokenizeToStringArray(extension, ";");
            String name = parts[0].trim();
            LinkedHashMap parameters = null;
            if(parts.length > 1) {
                parameters = new LinkedHashMap(parts.length - 1);

                for(int i = 1; i < parts.length; ++i) {
                    String parameter = parts[i];
                    int eqIndex = parameter.indexOf(61);
                    if(eqIndex != -1) {
                        String attribute = parameter.substring(0, eqIndex);
                        String value = parameter.substring(eqIndex + 1, parameter.length());
                        parameters.put(attribute, value);
                    }
                }
            }
            return new WebSocketExtension(name, parameters);
        }
    }

}
