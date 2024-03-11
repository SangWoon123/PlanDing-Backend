package com.tukorea.planding.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType {

    NAVER("NAVER"),
    KAKAO("KAKAO");

    private final String socialName;

    public static SocialType getSocialType(String registrationId) {
        if(KAKAO.socialName.equals(registrationId.toUpperCase())) {
            return SocialType.KAKAO;
        }
        return SocialType.NAVER;
    }
}
