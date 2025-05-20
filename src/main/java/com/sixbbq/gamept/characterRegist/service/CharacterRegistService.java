package com.sixbbq.gamept.characterRegist.service;

import com.sixbbq.gamept.api.dnf.service.DFService;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistRequestDto;
import com.sixbbq.gamept.characterRegist.dto.CharacterRegistResponseDto;
import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import com.sixbbq.gamept.characterRegist.repository.CharacterRegistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CharacterRegistService {
    private static final Logger log = LoggerFactory.getLogger(CharacterRegistService.class);
    private final CharacterRegistRepository characterRegistRepository;
    private final DFService dfService;

    @Autowired
    public CharacterRegistService(CharacterRegistRepository characterRegistRepository, DFService dfService) {
        this.characterRegistRepository = characterRegistRepository;
        this.dfService = dfService;
    }

    @Transactional
    public CharacterRegistResponseDto registerCharacter(String userId, CharacterRegistRequestDto requestDTO) {
        try {
            log.info("캐릭터 등록 요청: userId={}, serverId={}, characterName={}, adventureName={}", 
                userId, requestDTO.getServerId(), requestDTO.getCharacterName(), requestDTO.getAdventureName());

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
            Map<String, Object> characterDetail = dfService.getCharacterInfo(serverId, characterId);
            log.info("캐릭터 상세 정보: {}", characterDetail);

            // 5. 모험단명 일치 여부 확인
            String adventureName = (String) characterDetail.get("adventureName");
            log.info("모험단명 비교: 요청={}, 실제={}", requestDTO.getAdventureName(), adventureName);
            if (!requestDTO.getAdventureName().equals(adventureName)) {
                log.warn("모험단명 불일치: 요청={}, 실제={}", requestDTO.getAdventureName(), adventureName);
                return createErrorResponse("모험단명이 일치하지 않습니다.");
            }

            // 6. 중복 등록 검사 (전체 시스템 내에서)
            boolean existsByCurrentUser = characterRegistRepository.existsByUserIdAndCharacterId(userId, characterId);
            boolean existsByAnyUser = characterRegistRepository.existsByCharacterId(characterId);

            if (existsByCurrentUser) {
                log.warn("이미 등록된 캐릭터: userId={}, characterId={}", userId, characterId);
                return createErrorResponse("이미 등록된 캐릭터입니다.");
            } else if (existsByAnyUser) {
                log.warn("다른 사용자가 이미 등록한 캐릭터: characterId={}", characterId);
                return createErrorResponse("이미 다른 사용자가 등록한 캐릭터입니다.");
            }

            // 7. 캐릭터 정보 저장
            CharacterRegist userCharacter = new CharacterRegist();
            userCharacter.setUserId(userId);
            userCharacter.setCharacterId(characterId);
            userCharacter.setCharacterName(characterName);
            userCharacter.setServerId(serverId);
            userCharacter.setAdventureName(adventureName);
            userCharacter.setCreatedAt(LocalDateTime.now());

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
            response.setAdventureName(adventureName);
            
            log.info("캐릭터 등록 완료: {}", response);
            return response;

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
}
