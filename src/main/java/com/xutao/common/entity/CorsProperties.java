package com.xutao.common.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "chirstmas.cors")
public class CorsProperties {

    private List<String> allowOrigins;

    public CorsProperties() {
    }


    public CorsProperties(List<String> allowOrigins) {
        this.allowOrigins = allowOrigins;
    }

    public List<String> getAllowOrigins() {
        return allowOrigins;
    }

    public void setAllowOrigins(List<String> allowOrigins) {
        this.allowOrigins = allowOrigins;
    }


}
