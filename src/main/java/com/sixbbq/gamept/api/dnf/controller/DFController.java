package com.sixbbq.gamept.api.dnf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterInfoResponseAIDTO;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.api.dnf.dto.RequestAIDTO;
import com.sixbbq.gamept.api.dnf.dto.ResponseAIDTO;
import com.sixbbq.gamept.api.dnf.dto.request.ChatRequest;
import com.sixbbq.gamept.api.dnf.service.DFService;
import com.sixbbq.gamept.jwt.JwtTokenProvider;
import com.sixbbq.gamept.redis.service.RedisChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/df")
@RequiredArgsConstructor
public class DFController {
    private final RestTemplate restTemplate;
    private final JwtTokenProvider tokenProvider;
    @Value("${ai.url}")
    private String aiURL;

    private final DFService dfService;
    private final RedisChatService redisChatService;
    private static final String CHARACTER_KEY_PREFIX = "character";
    private static final String CHAT_KEY_PREFIX = "chat";
    private static final String RESPONSE_KEY_PREFIX = "response";

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
        log.info("/api/df/search : GET");
        log.info("server : {}, name : {}", server, name);
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
        log.info("/api/df/character : GET");
        log.info("server : {}, characterId : {}", server, characterId);
        try {
            DFCharacterResponseDTO characterInfo = redisChatService.getCharacterInfo(CHARACTER_KEY_PREFIX, characterId);
            if(characterInfo == null)
                return ResponseEntity.ok(dfService.getCharacterInfo(server, characterId));
            else
                return ResponseEntity.ok(characterInfo);
        } catch (Exception e) {
            throw new NoSuchElementException(e);
        }
    }

    /**
     * 3. 던파 캐릭터 정보 갱신 API
     * [요청 방식]
     * GET /api/df/character/refresh?server={serverId}&characterId={characterId}
     *
     * @param server      서버 ID (영문 식별자)
     * @param characterId 캐릭터 ID
     * @return            캐릭터 상세 정보 (JSON)
     */
    @GetMapping("/character/refresh")
    public ResponseEntity<?> refreshCharacterInfo(@RequestParam String server, @RequestParam String characterId) {
        log.info("/api/df/character/refresh : GET");
        log.info("server : {}, characterId : {}", server, characterId);
        try {
            return ResponseEntity.ok(dfService.getCharacterInfo(server, characterId));
        } catch (Exception e) {
            throw new NoSuchElementException(e);
        }
    }

    /**
     * 4. 캐릭터의 AI채팅 API
     * @param characterId 채팅창에 주체가 되는 캐릭터Id
     * @param questionMessage 질문 메세지
     * @return 질문에 대한 AI의 응답
     */
    @PostMapping("/chat")
    public ResponseEntity<?> addChat(@RequestParam String characterId, @RequestParam String questionMessage,
                                     HttpServletRequest request) {
        log.info("/api/df/chat : POST");
        log.info("characterId : {}, questionMessage : {}", characterId, questionMessage);
        try {
            List<String> getChat = redisChatService.getChat(CHAT_KEY_PREFIX, characterId);

            List<String> getResponse = redisChatService.getChat(RESPONSE_KEY_PREFIX, characterId);

            DFCharacterResponseDTO getCharacterInfo = redisChatService.getCharacterInfo(CHARACTER_KEY_PREFIX, characterId);
            DFCharacterInfoResponseAIDTO dto = new DFCharacterInfoResponseAIDTO(getCharacterInfo);
            String token = tokenProvider.extractJwtToken(request);

            RequestAIDTO requestDTO = new RequestAIDTO(questionMessage, token, dto, getChat, getResponse);

            // AI한테 데이터를 보내는 코드 작성
            String url = aiURL + "/api/df/chat";
            ResponseEntity<String> getResponseAI = restTemplate.postForEntity(url, requestDTO, String.class);
            String json = getResponseAI.getBody();

            ObjectMapper mapper = new ObjectMapper();
            ResponseAIDTO aiDTO = mapper.readValue(json, ResponseAIDTO.class);

            // 사용자 메시지 저장
            redisChatService.addChatMessage(CHAT_KEY_PREFIX, characterId, questionMessage);
            // AI 응답 저장
            redisChatService.addChatMessage(RESPONSE_KEY_PREFIX, characterId, aiDTO.getAnswer());

            // 이번이 4번째 채팅일 경우 초기화
            if(getChat.size() >= 3) {
                Map<String,String> response = new HashMap<>();
                response.put("message", "한도를 초과하였습니다. 채팅방이 초기화 됩니다.");
                return ResponseEntity.ok().body(response);
            }

            return ResponseEntity.ok(Map.of("response", aiDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "채팅 메시지 처리 실패"));
        }
    }

    /**
     * 5. 캐릭터 AI채팅 내역 초기화
     * @param characterId 채팅내역을 초기화할 캐릭터
     * @return 채팅 초기화 여부
     */
    @DeleteMapping("/chat")
    public ResponseEntity<?> deleteChat(@RequestParam String characterId) {
        log.info("/api/df/chat : DELETE");
        log.info("characterId : {}", characterId);
        try {
            redisChatService.clearChat(CHAT_KEY_PREFIX, characterId);
            redisChatService.clearChat(RESPONSE_KEY_PREFIX, characterId);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "실패!");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "초기화에 성공했습니다.");
        return ResponseEntity.ok().body(response);
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
