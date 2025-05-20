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

    /**
     * redis에 List 구조로 채팅 내역 저장
     * @param characterId 저장할 키값[캐릭터id]
     * @param message 저장할 메세지
     */
    public void addChatMessage(String characterId, String message) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(characterId, message);
        redisTemplate.expire(characterId, EXPIRED_DAY, TimeUnit.DAYS);
    }

    /**
     * redis에 저장된 채팅 내역 불러오기
     * @param characterId 불러올 키값[캐릭터id]
     * @return 저장된 메세지
     */
    public List<String> getChat(String characterId) {
        return redisTemplate.opsForList().range(characterId, 0, -1);
    }


    /**
     * redis에 저장된 채팅 내역 삭제
     * @param characterId 삭제할 키값[캐릭터id]
     */
    public void clearChat(String characterId) {
        redisTemplate.delete(characterId);
    }
}
