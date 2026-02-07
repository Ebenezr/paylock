package com.blind.paylock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.headers")
public class HeaderProperties {
    private String requestRefId;
    private String organization;
    private String channel;
    private String userId;
}
