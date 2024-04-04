package com.tukorea.planding.global.jwt.token.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties()
public class JwtProperities {
    private String accessHeader;
    private String refreshHeader;
    private long accessExpiration;
    private long refreshExpiration;
}
