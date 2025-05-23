package com.sixbbq.gamept.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterInfoResponseAIDTO;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.redis.dto.ChatMessageDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisChatService {
    private static final int EXPIRED_DAY = 1;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * redis에 List 구조로 채팅 내역 저장
     * @param characterId 저장할 키값[캐릭터id]
     * @param message 저장할 메세지
     */
    public void addChatMessage(String suffixKey, String characterId, String message) {
        if (!StringUtils.hasText(characterId) || !StringUtils.hasText(message)) {
            log.warn("Invalid input: characterId={}, message={}", characterId, message);
            return;
        }

        String redisKey = suffixKey + ":" + characterId;
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(redisKey, message);
        redisTemplate.expire(redisKey, EXPIRED_DAY, TimeUnit.DAYS);
        log.debug("Added chat message to Redis: [{}] -> {}", redisKey, message);
    }

    /**
     * redis에 저장된 채팅 내역 불러오기
     * @param characterId 불러올 키값[캐릭터id]
     * @return 저장된 메세지
     */
    public List<String> getChat(String suffixKey, String characterId) {
        if (!StringUtils.hasText(characterId)) {
            log.warn("Invalid characterId for getChat: {}", characterId);
            return List.of();
        }

        String redisKey = suffixKey + ":" + characterId;
        List<String> chat = redisTemplate.opsForList().range(redisKey, 0, -1);
        log.debug("Fetched chat messages from Redis: [{}] -> {} messages", redisKey, chat != null ? chat.size() : 0);
        return chat;
    }

    /**
     * redis에 저장된 채팅 내역 삭제
     * @param characterId 삭제할 키값[캐릭터id]
     */
    public void clearChat(String suffixKey, String characterId) {
        if (!StringUtils.hasText(characterId)) {
            log.warn("Invalid characterId for clearChat: {}", characterId);
            return;
        }

        String redisKey = suffixKey + ":" + characterId;
        redisTemplate.delete(redisKey);
        log.debug("Cleared chat messages from Redis: [{}]", redisKey);
    }

    /**
     * Redis에 캐릭터 정보를 저장하는 메서드
     * @param key 저장할 키 (예: "character:characterId")
     * @param value 저장할 캐릭터 정보 (Map 형식)
     */
    public void setCharacterInfo(String key, DFCharacterResponseDTO value) {
        try {
            DFCharacterInfoResponseAIDTO dfCharacterInfoResponseAIDTO = new DFCharacterInfoResponseAIDTO(value);
            String jsonCharacterInfo = objectMapper.writeValueAsString(dfCharacterInfoResponseAIDTO);
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, EXPIRED_DAY, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(value.getCharacterId(), jsonCharacterInfo, EXPIRED_DAY, TimeUnit.DAYS);
            log.debug("캐릭터 정보 Redis 저장 완료: [{}]", key);
        } catch (Exception e) {
            log.error("캐릭터 정보 Redis 저장 실패: {}", e.getMessage());
        }
    }

    public DFCharacterResponseDTO getCharacterInfo(String suffixKey, String key) {
        try {
            String redisKey = suffixKey + ":" + key;
            String json = redisTemplate.opsForValue().get(redisKey);
            if (json != null) {
                DFCharacterResponseDTO value = objectMapper.readValue(json, DFCharacterResponseDTO.class);
                log.debug("캐릭터 정보 Redis에서 조회 완료: [{}]", key);
                return value;
            } else {
                log.warn("캐릭터 정보 Redis에 없음: [{}]", key);
                return null;
            }
        } catch (Exception e) {
            log.error("캐릭터 정보 Redis 조회 실패: {}", e.getMessage());
            return null;
        }
    }

}
