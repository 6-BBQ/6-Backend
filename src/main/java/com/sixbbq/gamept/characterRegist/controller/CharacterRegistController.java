package com.sixbbq.gamept.characterRegist.controller;

import com.sixbbq.gamept.characterRegist.dto.CharacterRegistRequestDto;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistResponseDto;
import com.sixbbq.gamept.characterRegist.service.CharacterRegistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/characters")
public class CharacterRegistController {
    private static final Logger log = LoggerFactory.getLogger(CharacterRegistController.class);
    private final CharacterRegistService characterService;

    @Autowired
    public CharacterRegistController(CharacterRegistService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<?> registerCharacter(HttpSession session, @RequestBody CharacterRegistRequestDto requestDTO) {
        // 1. 로그인 상태 확인
        String userId = (String) session.getAttribute("LOGGED_IN_MEMBER_ID");
        log.info("캐릭터 등록 요청: userId={}, requestData={}", userId, requestDTO);
        
        if (userId == null) {
            log.warn("비로그인 상태에서 캐릭터 등록 시도");
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 2. 서비스 호출
        CharacterRegistResponseDto result = characterService.registerCharacter(userId, requestDTO);
        log.info("캐릭터 등록 결과: {}", result);

        // 3. 응답 반환
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
