package com.tukorea.planding.domain.invite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RedisGroupInviteService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void createInvitation(GroupInviteDTO inviteDTO) {
        String key = "invite:" + inviteDTO.getInviteCode();
        String value = convertObjectToJson(inviteDTO);

        if (value != null) {
            redisTemplate.opsForValue().set(key, value, Duration.between(LocalDateTime.now(), inviteDTO.getExpiredAt()));
        }
    }

    public GroupInviteDTO getInvitation(String inviteCode) {
        String key = "invite:" + inviteCode;
        String value = redisTemplate.opsForValue().get(key);

        return convertJsonToObject(value, GroupInviteDTO.class);
    }

    public void deleteInvitation(String inviteCode) {
        String key = "invite:" + inviteCode;
        redisTemplate.delete(key);
    }

    private String convertObjectToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private <T> T convertJsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }
}
