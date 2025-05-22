package com.sixbbq.gamept.redis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbbq.gamept.redis.dto.ChatMessageDto;
import com.sixbbq.gamept.redis.service.RedisChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/redis")
public class RedisTestController {

    private final RedisChatService redisChatService;
    private final ObjectMapper objectMapper;

//    @GetMapping("/test")
//    public ResponseEntity<?> testConnection() {
//        try {
//            String testValue = redisChatService.getChat("springTestKey").stream().findFirst().orElse(null);
//            Map<String, Object> response = Map.of(
//                    "status", "success",
//                    "message", "Redis 연결 성공",
//                    "value", testValue
//            );
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Redis 연결 테스트 실패", e);
//            return ResponseEntity.badRequest().body("Redis 연결 실패: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/test")
//    public ResponseEntity<?> setTestValue(@RequestParam String value) {
//        try {
//            redisChatService.clearChat("springTestKey");
//            redisChatService.addChatMessage("springTestKey", value);
//
//            Map<String, Object> response = Map.of(
//                    "status", "success",
//                    "message", "Redis 값 설정 성공",
//                    "value", value
//            );
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Redis 값 설정 실패", e);
//            return ResponseEntity.badRequest().body("Redis 값 설정 실패: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/chat/dummy")
//    public ResponseEntity<?> createDummyChat(@RequestParam String characterId) {
//        try {
//            redisChatService.addDummyChatMessages(characterId);
//            List<String> messages = redisChatService.getChat(characterId);
//
//            Map<String, Object> response = Map.of(
//                    "status", "success",
//                    "message", "더미 채팅 데이터 생성 성공",
//                    "characterId", characterId,
//                    "messageCount", messages.size()
//            );
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("더미 채팅 데이터 생성 실패", e);
//            return ResponseEntity.badRequest().body("더미 채팅 데이터 생성 실패: " + e.getMessage());
//        }
//    }
//
//    @GetMapping("/chat/dummy")
//    public ResponseEntity<?> getDummyChat(@RequestParam String characterId) {
//        try {
//            List<String> jsonMessages = redisChatService.getChat(characterId);
//            List<ChatMessageDto> messages = new ArrayList<>();
//
//            for (String json : jsonMessages) {
//                try {
//                    ChatMessageDto message = objectMapper.readValue(json, ChatMessageDto.class);
//                    messages.add(message);
//                } catch (Exception ex) {
//                    log.warn("Failed to parse chat message JSON: {}", ex.getMessage());
//                }
//            }
//
//            Map<String, Object> response = Map.of(
//                    "status", "success",
//                    "characterId", characterId,
//                    "messages", messages
//            );
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("더미 채팅 데이터 조회 실패", e);
//            return ResponseEntity.badRequest().body("더미 채팅 데이터 조회 실패: " + e.getMessage());
//        }
//    }
}
