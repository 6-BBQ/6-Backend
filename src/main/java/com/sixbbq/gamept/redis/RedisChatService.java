package com.sixbbq.gamept.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisChatService {
    private static final int EXPIRED_DAY = 1;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addChatMessage(String characterId, String message) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(characterId, message);
        redisTemplate.expire(characterId, EXPIRED_DAY, TimeUnit.DAYS);
    }

    public List<String> getChat(String userId) {
        return redisTemplate.opsForList().range(userId, 0, -1);
    }


    public void clearChat(String characterId) {
        redisTemplate.delete(characterId);
    }
}
