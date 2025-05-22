package com.sixbbq.gamept.api.dnf.controller;

import com.sixbbq.gamept.api.dnf.service.DFService;
import com.sixbbq.gamept.redis.service.RedisChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/df")
@RequiredArgsConstructor
public class DFController {

    private final DFService dfService;
    private final RedisChatService redisChatService;

    /**
     * 1. 던파 캐릭터 검색 API
     *
     * [요청 방식]
     * GET /api/df/search?server={serverId}&name={characterNameOrAdventureName}
     *
     * @param server 서버 ID (영문 식별자, 목록 참조. "adven"일 경우 모험단 검색)
     * @param name   캐릭터 이름 또는 모험단명 (server가 "adven"일 경우 모험단명)
     * @return       검색된 캐릭터 목록 또는 단일 캐릭터 정보 (JSON)
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCharacter(@RequestParam String server, @RequestParam String name) {
        try {
            return ResponseEntity.ok(dfService.processSearchRequest(server, name));
        } catch (Exception e) {
            throw new NoSuchElementException(e);
        }
    }

    /**
     * 2. 던파 캐릭터 상세 조회 API
     *
     * [요청 방식]
     * GET /api/df/character?server={serverId}&characterId={characterId}
     *
     * @param server      서버 ID (영문 식별자)
     * @param characterId 캐릭터 ID
     * @return            캐릭터 상세 정보 (JSON)
     */
    @GetMapping("/character")
    public ResponseEntity<?> getCharacterInfo(@RequestParam String server, @RequestParam String characterId) {
        try {
            return ResponseEntity.ok(dfService.getCharacterInfo(server, characterId));
        } catch (Exception e) {
            throw new NoSuchElementException(e);
        }

    }

    @PostMapping("/ai/addchat")
    public ResponseEntity<?> addChat(@RequestParam String characterId, @RequestParam String chatMessage) {
        try {
            // 사용자 메시지 저장
            redisChatService.addChatMessage(characterId, chatMessage);


            // 임시로 하드코딩된 응답 사용
            String aiResponse = "안녕하세요! 저는 당신의 던전앤파이터 AI 어시스턴트입니다. 무엇을 도와드릴까요?";

            // AI 응답 저장
            redisChatService.addChatMessage(characterId, aiResponse);

            return ResponseEntity.ok(Map.of("response", aiResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "채팅 메시지 처리 실패"));
        }
    }

    @DeleteMapping("/ai/deletechat")
    public ResponseEntity<?> deleteChat(@RequestParam String characterId) {
        redisChatService.clearChat(characterId);

        return ResponseEntity.ok().build();
    }

    /**
     * 에러 발생 시 오게되는 공통 Exception
     * @param e 처리할 exception
     * @return 404코드로 에러 데이터 넣어서 처리
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleException(Exception e) {
        Map<String, String> error = Map.of("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
