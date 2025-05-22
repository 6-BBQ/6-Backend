package com.sixbbq.gamept.redis.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTestRunner implements CommandLineRunner {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(String... args) {
        try {
            // Redis 연결 테스트
            redisTemplate.opsForValue().set("springTestKey", "Redis 연결 테스트 성공!");
            String value = redisTemplate.opsForValue().get("springTestKey");

            if (value != null && value.equals("Redis 연결 테스트 성공!")) {
                log.info("Redis 연결 테스트 성공: {}", value);
            } else {
                log.error("Redis 연결 테스트 실패: 예상치 못한 값");
            }
        } catch (Exception e) {
            log.error("Redis 연결 테스트 실패: {}", e.getMessage());
        }
    }
}