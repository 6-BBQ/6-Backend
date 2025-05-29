package com.sixbbq.gamept.api.dnf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterAuctionResponseDTO;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterInfoResponseAIDTO;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.api.dnf.dto.RequestAIDTO;
import com.sixbbq.gamept.api.dnf.dto.ResponseAIDTO;
import com.sixbbq.gamept.api.dnf.dto.request.SpecCheckRequestDTO;
import com.sixbbq.gamept.api.dnf.dto.response.SpecCheckResponseDTO;
import com.sixbbq.gamept.api.dnf.service.DFService;
import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import com.sixbbq.gamept.characterRegist.service.CharacterRegistService;
import com.sixbbq.gamept.jwt.JwtTokenProvider;
import com.sixbbq.gamept.redis.service.RedisChatService;
import com.sixbbq.gamept.util.ErrorUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    @Value("${discord.admin-name}")
    private String discordAdminName;

    private final DFService dfService;
    private final RedisChatService redisChatService;
    private final CharacterRegistService characterRegistService;

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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            CharacterRegist originalRegist = characterRegistService.getCharacters(userId, characterId);

            // 하루가 지났다면 질문횟수 초기화 하기
            CharacterRegist afterRegist = characterRegistService.characterAIStackCheck(originalRegist);

            // 이전에 5번의 채팅기록이 존재한다면 채팅 거부하기
            if(afterRegist.getAiRequestCount() >= 5) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "현재 채팅의 한도에 도달했습니다. 새 채팅을 만들어주세요!");

                redisChatService.clearChat(CHAT_KEY_PREFIX, afterRegist.getCharacterId());
                redisChatService.clearChat(CHARACTER_KEY_PREFIX, afterRegist.getCharacterId());

                return ResponseEntity.ok().body(response);
            }

            DFCharacterResponseDTO getCharacterInfo = redisChatService.getCharacterInfo(CHARACTER_KEY_PREFIX, characterId);
            DFCharacterInfoResponseAIDTO dto = new DFCharacterInfoResponseAIDTO(getCharacterInfo);
            String token = tokenProvider.extractJwtToken(request);

            List<String> getChat = redisChatService.getChat(CHAT_KEY_PREFIX, characterId);
            List<String> getResponse = redisChatService.getChat(RESPONSE_KEY_PREFIX, characterId);

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
            // AI 사용 횟수 증가
            characterRegistService.plusAICount(afterRegist);
            aiDTO.setAiRequestCount(afterRegist.getAiRequestCount());

            return ResponseEntity.ok().body(aiDTO);
        } catch (Exception e) {
            ErrorUtil.logError(e, request, discordAdminName);
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", "채팅 메시지 처리 실패"));
        }
    }

    /**
     * 5. 캐릭터 AI채팅 내역 초기화
     * @param characterId 채팅내역을 초기화할 캐릭터
     * @return 채팅 초기화 성공 여부
     */
    @DeleteMapping("/chat")
    public ResponseEntity<?> deleteChat(@RequestParam String characterId, HttpServletRequest request) {
        log.info("/api/df/chat : DELETE");
        log.info("characterId : {}", characterId);
        try {
            redisChatService.clearChat(CHAT_KEY_PREFIX, characterId);
            redisChatService.clearChat(RESPONSE_KEY_PREFIX, characterId);
        } catch (Exception e) {
            ErrorUtil.logError(e, request, discordAdminName);
            Map<String, String> response = new HashMap<>();
            response.put("message", "채팅내역 초기화 실패");
            return ResponseEntity.badRequest().body(response);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "채팅내역 초기화에 성공했습니다.");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/spec-check")
    public ResponseEntity<?> checkSpec(@Validated @RequestBody SpecCheckRequestDTO requestDTO,
                                       BindingResult result) {
        log.info("/api/df/spec-check : GET");
        log.info("requestDTO : {}", requestDTO);

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors().get(0).getDefaultMessage());
        }

        SpecCheckResponseDTO responseResult = dfService.specCheck(requestDTO);

        return null;
    }

    /**
     * 6. 캐릭터 아이템 가격 비교 API
     * [요청 방식]
     * GET /api/df/character/compare?server={serverId}&characterId={characterId}&compareServer={compareServerId}&compareCharacterId={compareCharacterId}
     *
     * @param server 서버 ID (영문 식별자)
     * @param characterId 캐릭터 ID
     * @param compareServer 비교할 캐릭터의 서버 ID
     * @param compareCharacterId 비교할 캐릭터의 ID
     * @return 두 캐릭터의 아이템 가격 비교 정보
     */
    @GetMapping("/character/compare")
    public ResponseEntity<?> compareCharacterItems(
            @RequestParam String server,
            @RequestParam String characterId,
            @RequestParam String compareServer,
            @RequestParam String compareCharacterId) {
        log.info("/api/df/character/compare : GET");
        log.info("server: {}, characterId: {}, compareServer: {}, compareCharacterId: {}",
                server, characterId, compareServer, compareCharacterId);

        try {
            // 현재 캐릭터 정보 조회
            DFCharacterResponseDTO currentCharacter = dfService.getCharacterInfo(server, characterId);
            DFCharacterInfoResponseAIDTO currentAI = new DFCharacterInfoResponseAIDTO(currentCharacter);

            // 비교할 캐릭터 정보 조회
            DFCharacterResponseDTO compareCharacter = dfService.getCharacterInfo(compareServer, compareCharacterId);
            DFCharacterInfoResponseAIDTO compareAI = new DFCharacterInfoResponseAIDTO(compareCharacter);

            // 경매장 정보 DTO 생성
            DFCharacterAuctionResponseDTO currentAuction = new DFCharacterAuctionResponseDTO();
            DFCharacterAuctionResponseDTO compareAuction = new DFCharacterAuctionResponseDTO();

            // 크리쳐 가격 조회
            if (currentAI.getCreatureName() != null) {
                currentAuction.setCreaturePrice(dfService.getAuctionPrice(currentAI.getCreatureName()));
            }
            if (compareAI.getCreatureName() != null) {
                compareAuction.setCreaturePrice(dfService.getAuctionPrice(compareAI.getCreatureName()));
            }

            // 칭호 가격 조회
            if (currentAI.getTitleName() != null) {
                currentAuction.setTitlePrice(dfService.getAuctionPrice(currentAI.getTitleName()));
            }
            if (compareAI.getTitleName() != null) {
                compareAuction.setTitlePrice(dfService.getAuctionPrice(compareAI.getTitleName()));
            }

            // 오라 가격 조회
            if (currentAI.getAuraName() != null) {
                currentAuction.setAuraPrice(dfService.getAuctionPrice(currentAI.getAuraName()));
            }
            if (compareAI.getAuraName() != null) {
                compareAuction.setAuraPrice(dfService.getAuctionPrice(compareAI.getAuraName()));
            }

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("currentCharacter", Map.of(
                    "characterInfo", currentCharacter,
                    "auctionInfo", currentAuction
            ));
            response.put("compareCharacter", Map.of(
                    "characterInfo", compareCharacter,
                    "auctionInfo", compareAuction
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("캐릭터 아이템 비교 실패: {}", e.getMessage());
            throw new NoSuchElementException("캐릭터 아이템 비교 중 오류가 발생했습니다.");
        }
    }


    /**
     * 에러 발생 시 오게되는 공통 Exception
     * @param e 처리할 exception
     * @return 404코드로 에러 데이터 넣어서 처리
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleException(Exception e, HttpServletRequest request) {
        ErrorUtil.logError(e, request, discordAdminName);
        Map<String, String> error = Map.of("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
