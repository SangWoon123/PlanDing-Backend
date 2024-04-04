package com.tukorea.planding.global.jwt.token.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String SECRET_KEY;
    private String accessHeader;
    private String refreshHeader;
    private long accessExpiration;
    private long refreshExpiration;
}
