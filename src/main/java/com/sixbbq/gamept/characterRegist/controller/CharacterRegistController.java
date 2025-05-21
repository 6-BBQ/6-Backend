package com.sixbbq.gamept.characterRegist.controller;

import com.sixbbq.gamept.characterRegist.dto.CharacterRegistRequestDto;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistResponseDto;
import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import com.sixbbq.gamept.characterRegist.service.CharacterRegistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    /**
     * 모험단에 속한 캐릭터 제거
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteCharacter(HttpSession session, @RequestParam String characterId) {
        // 1. 로그인 상태 확인
        String userId = (String) session.getAttribute("LOGGED_IN_MEMBER_ID");
        log.info("캐릭터 삭제 요청: userId={}, requestData={}", userId, characterId);

        if (userId == null) {
            log.warn("비로그인 상태에서 캐릭터 삭제 시도");
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        boolean result = characterService.deleteCharacter(userId, characterId);

        if(result) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "삭제 성공");
            return ResponseEntity.ok().body(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "삭제 실패");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 계정에 속한 캐릭터 조회
     */
    @GetMapping("/adventure")
    public ResponseEntity<?> getAdventureCharacters(HttpSession session) {
        String userId = (String) session.getAttribute("LOGGED_IN_MEMBER_ID");
        log.info("모험단 캐릭터 조회 요청: userId={}, adventureName={}", userId);

        if (userId == null) {
            log.warn("비로그인 상태에서 모험단 캐릭터 조회 시도");
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            List<CharacterRegist> charactersByUserId = characterService.getCharactersByAdventureName(userId);

            if(charactersByUserId.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "등록된 캐릭터가 없습니다.");
                return ResponseEntity.ok().body(response);
            } else
                return ResponseEntity.ok().body(charactersByUserId);
        } catch (Exception e) {
            log.error("모험단 캐릭터 조회 오류: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "모험단 캐릭터 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
