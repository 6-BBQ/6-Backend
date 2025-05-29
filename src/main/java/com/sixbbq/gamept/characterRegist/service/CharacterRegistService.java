package com.sixbbq.gamept.characterRegist.service;

import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.api.dnf.service.DFService;
import com.sixbbq.gamept.api.dnf.util.DFUtil;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistDTO;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistRequestDto;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistResponseDto;
import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import com.sixbbq.gamept.characterRegist.repository.CharacterRegistRepository;
import com.sixbbq.gamept.redis.service.RedisChatService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class CharacterRegistService {
    private static final Logger log = LoggerFactory.getLogger(CharacterRegistService.class);
    private final CharacterRegistRepository characterRegistRepository;
    private final DFService dfService;
    private final RedisChatService redisChatService;

    @Value("${dnf.api.character-image-base-url}")
    private String CHARACTER_IMAGE_BASE_URL;
    private static final String CHARACTER_KEY_PREFIX = "character";
    private static final String CHAT_KEY_PREFIX = "chat";

    @Autowired
    public CharacterRegistService(CharacterRegistRepository characterRegistRepository, DFService dfService,
                                  RedisChatService redisChatService) {
        this.characterRegistRepository = characterRegistRepository;
        this.dfService = dfService;
        this.redisChatService = redisChatService;
    }

    @Transactional
    public CharacterRegistResponseDto registerCharacter(String userId, CharacterRegistRequestDto requestDTO) {
        try {
            log.info("캐릭터 등록 요청: userId={}, serverId={}, characterName={}",
                userId, requestDTO.getServerId(), requestDTO.getCharacterName());

            // 1. 던파 API를 통해 캐릭터 검색 (DFService의 실제 메서드명 사용)
            Map<String, Object> searchResult = dfService.processSearchRequest(requestDTO.getServerId(), requestDTO.getCharacterName());
            log.info("API 검색 결과: {}", searchResult);

            // 2. 검색 결과 유효성 검증
            List<Map<String, Object>> rows = (List<Map<String, Object>>) searchResult.get("rows");
            if (rows == null || rows.isEmpty()) {
                log.warn("캐릭터를 찾을 수 없습니다: {}", requestDTO.getCharacterName());
                return createErrorResponse("캐릭터를 찾을 수 없습니다.");
            }

            // 3. 첫 번째 캐릭터 정보 가져오기
            Map<String, Object> character = rows.get(0);
            String characterId = (String) character.get("characterId");
            String characterName = (String) character.get("characterName");
            String serverId = (String) character.get("serverId");
            log.info("찾은 캐릭터: id={}, name={}, server={}", characterId, characterName, serverId);

            // 4. 캐릭터 상세 정보 조회
            DFCharacterResponseDTO characterDetail = dfService.getCharacterInfo(serverId, characterId);
            log.info("캐릭터 상세 정보: {}", characterDetail);

            // 5. 모험단명 일치 여부 확인
            List<CharacterRegist> byUserId = characterRegistRepository.findByUserId(userId);
            CharacterRegist userCharacter = new CharacterRegist();
            if(byUserId == null || byUserId.isEmpty()) {
                // 신규등록
                userCharacter.setUserId(userId);
                userCharacter.setCharacterId(characterId);
                userCharacter.setCharacterName(characterName);
                userCharacter.setServerId(serverId);
                userCharacter.setAdventureName(characterDetail.getAdventureName());
                userCharacter.setCreatedAt(LocalDateTime.now());
            } else if (byUserId.get(0).getAdventureName().equals(characterDetail.getAdventureName())) {
                userCharacter.setUserId(userId);
                userCharacter.setCharacterId(characterId);
                userCharacter.setCharacterName(characterName);
                userCharacter.setServerId(serverId);
                userCharacter.setAdventureName(characterDetail.getAdventureName());
                userCharacter.setCreatedAt(LocalDateTime.now());
            }  else {
                return new CharacterRegistResponseDto(false,"모험단명이 일치하지 않습니다.");
            }

            // 6. 중복 등록 검사 (전체 시스템 내에서)
            boolean existsByCurrentUser = characterRegistRepository.existsByUserIdAndCharacterId(userId, characterId);

            if (existsByCurrentUser) {
                log.warn("이미 등록된 캐릭터: userId={}, characterId={}", userId, characterId);
                return createErrorResponse("이미 등록된 캐릭터입니다.");
            }

            log.info("저장할 캐릭터 정보: {}", userCharacter);
            
            // 저장 시도 및 결과 로깅
            try {
                CharacterRegist savedCharacter = characterRegistRepository.save(userCharacter);
                log.info("캐릭터 저장 성공: id={}", savedCharacter.getId());
            } catch (Exception e) {
                log.error("캐릭터 저장 실패: {}", e.getMessage(), e);
                throw e;
            }

            // 8. 응답 반환
            CharacterRegistResponseDto response = new CharacterRegistResponseDto();
            response.setSuccess(true);
            response.setMessage("캐릭터가 성공적으로 등록되었습니다.");
            response.setCharacterId(characterId);
            response.setCharacterName(characterName);
            response.setServerId(serverId);
            response.setAdventureName(characterDetail.getAdventureName());
            
            log.info("캐릭터 등록 완료: {}", response);
            return response;

        } catch (HttpClientErrorException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "조회에 실패했습니다.");
        }
        catch (HttpServerErrorException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "던파 API 서버에 연결하지 못했습니다.");
        }
        catch (RestClientException e) {
            throw new RestClientException("던파API 서버와의 응답이 실패했습니다.");
        } catch (NoSuchElementException e) {
            log.error("캐릭터 검색 오류: {}", e.getMessage(), e);
            return createErrorResponse("캐릭터 검색 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("캐릭터 등록 오류: {}", e.getMessage(), e);
            return createErrorResponse("캐릭터 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 에러 응답 생성 헬퍼 메서드
    private CharacterRegistResponseDto createErrorResponse(String message) {
        CharacterRegistResponseDto response = new CharacterRegistResponseDto();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    /**
     * 유저 아이디로 사용자의 캐릭터 조회
     */
    @Transactional
    public List<CharacterRegistDTO> getCharactersByAdventureName(String userId) {
        log.info("모험단별 캐릭터 조회: userId={}", userId);

        List<CharacterRegist> searchCharacterList = characterRegistRepository.findByUserId(userId);
        List<CharacterRegistDTO> characterList = new ArrayList<>();

        for(CharacterRegist character : searchCharacterList) {
            CharacterRegist regist = characterAIStackCheck(character);
            String imgUrl = DFUtil.buildCharacterImageUrl(CHARACTER_IMAGE_BASE_URL, character.getServerId(), character.getCharacterId(), 1);
            characterList.add(new CharacterRegistDTO(regist, imgUrl));

            redisChatService.clearChat(CHAT_KEY_PREFIX, character.getCharacterId());
            redisChatService.clearChat(CHARACTER_KEY_PREFIX, character.getCharacterId());
        }
        return characterList;
    }

    public boolean deleteCharacter(String userId, String characterId) {
        Optional<CharacterRegist> byUserIdAndCharacterId = characterRegistRepository.findByUserIdAndCharacterId(userId, characterId);
        if (byUserIdAndCharacterId.isPresent()) {
            characterRegistRepository.delete(byUserIdAndCharacterId.get());
            return true;
        } else {
            return false;
        }
    }

    public CharacterRegist getCharacters(String userId, String characterId) {
        Optional<CharacterRegist> findCharacter = characterRegistRepository.findByUserIdAndCharacterId(userId, characterId);
        if (findCharacter.isPresent()) {
            return findCharacter.get();
        } else {
            throw new NoSuchElementException("캐릭터를 찾을 수 없습니다!");
        }
    }

    public void plusAICount(CharacterRegist regist) {
        if(regist.getAiRequestTime() == null)
            regist.setAiRequestTime(LocalDateTime.now());
        regist.setAiRequestCount(regist.getAiRequestCount() + 1);

        characterRegistRepository.save(regist);
    }

    // 하루가 지났는지 체크하여 초기화 여부를 확인하는 메서드
    public CharacterRegist characterAIStackCheck(CharacterRegist character) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime aiTime = character.getAiRequestTime();
        if(aiTime != null) {
            LocalDateTime thresholdTime = aiTime.toLocalDate().plusDays(1).atStartOfDay();

            if(now.isAfter(thresholdTime)) {
                character.setAiRequestTime(null);
                character.setAiRequestCount(0);
                CharacterRegist save = characterRegistRepository.save(character);

                return save;
            }
        }
        return character;
    }
}
