package com.sixbbq.gamept.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sixbbq.gamept.redis.dto.ChatMessageDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisChatService {
    private static final int EXPIRED_DAY = 1;
    private static final String CHAT_KEY_PREFIX = "chat:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private String buildChatKey(String characterId) {
        return CHAT_KEY_PREFIX + characterId;
    }

    /**
     * redis에 List 구조로 채팅 내역 저장
     * @param characterId 저장할 키값[캐릭터id]
     * @param message 저장할 메세지
     */
    public void addChatMessage(String characterId, String message) {
        if (!StringUtils.hasText(characterId) || !StringUtils.hasText(message)) {
            log.warn("Invalid input: characterId={}, message={}", characterId, message);
            return;
        }

        String redisKey = buildChatKey(characterId);
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
    public List<String> getChat(String characterId) {
        if (!StringUtils.hasText(characterId)) {
            log.warn("Invalid characterId for getChat: {}", characterId);
            return List.of();
        }

        String redisKey = buildChatKey(characterId);
        List<String> chat = redisTemplate.opsForList().range(redisKey, 0, -1);
        log.debug("Fetched chat messages from Redis: [{}] -> {} messages", redisKey, chat != null ? chat.size() : 0);
        return chat;
    }

    /**
     * redis에 저장된 채팅 내역 삭제
     * @param characterId 삭제할 키값[캐릭터id]
     */
    public void clearChat(String characterId) {
        if (!StringUtils.hasText(characterId)) {
            log.warn("Invalid characterId for clearChat: {}", characterId);
            return;
        }

        String redisKey = buildChatKey(characterId);
        redisTemplate.delete(redisKey);
        log.debug("Cleared chat messages from Redis: [{}]", redisKey);
    }

    /**
     * 특정 캐릭터 ID에 대해 더미 채팅 메시지 여러 개를 한번에 추가하는 메서드
     * @param characterId 캐릭터 ID
     */
    public void addDummyChatMessages(String characterId) {
        if (!StringUtils.hasText(characterId)) {
            log.warn("Invalid characterId for addDummyChatMessages: {}", characterId);
            return;
        }

        String redisKey = buildChatKey(characterId);
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        // 더미 메시지 ChatMessageDto 리스트 생성
        List<ChatMessageDto> dummyMessages = List.of(
                new ChatMessageDto("안녕하세요! 환영합니다.", "system", characterId, LocalDateTime.now()),
                new ChatMessageDto("오늘은 어떤 모험을 떠날까요?", "system", characterId, LocalDateTime.now()),
                new ChatMessageDto("조심하세요, 몬스터가 나타났어요!", "system", characterId, LocalDateTime.now()),
                new ChatMessageDto("잘했어요! 경험치를 얻었습니다.", "system", characterId, LocalDateTime.now()),
                new ChatMessageDto("다음 레벨까지 힘내세요!", "system", characterId, LocalDateTime.now()),
                new ChatMessageDto("아이템을 획득했습니다.", "system", characterId, LocalDateTime.now()),
                new ChatMessageDto("채팅 더미 데이터가 성공적으로 추가되었습니다.", "system", characterId, LocalDateTime.now())
        );

        // 기존 채팅 삭제
        redisTemplate.delete(redisKey);

        // 각 메시지를 JSON으로 직렬화해서 Redis에 저장
        dummyMessages.forEach(msg -> {
            try {
                String json = objectMapper.writeValueAsString(msg);
                listOps.rightPush(redisKey, json);
            } catch (Exception e) {
                log.error("Failed to serialize dummy message: {}", e.getMessage());
            }
        });

        redisTemplate.expire(redisKey, EXPIRED_DAY, TimeUnit.DAYS);

        log.info("Added dummy chat messages for characterId [{}]", characterId);
    }

    /**
     * Redis에 캐릭터 정보를 저장하는 메서드
     * @param key 저장할 키 (예: "character:characterId")
     * @param value 저장할 캐릭터 정보 (Map 형식)
     */
    public void setCharacterInfo(String key, Map<String, Object> value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, EXPIRED_DAY, TimeUnit.DAYS);
            log.debug("캐릭터 정보 Redis 저장 완료: [{}]", key);
        } catch (Exception e) {
            log.error("캐릭터 정보 Redis 저장 실패: {}", e.getMessage());
        }
    }

}
