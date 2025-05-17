package com.sixbbq.gamept.maple.service;

import com.sixbbq.gamept.maple.entity.MapleCharacter;
import com.sixbbq.gamept.maple.repository.MapleCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MapleService {

    private final RestTemplate restTemplate;
    private final MapleCharacterRepository characterRepository;

    @Value("${maplestory.api.key}")
    private String apiKey;
    
    @Value("${maplestory.api.base-url}")
    private String baseUrl;

    /**
     * 캐릭터명으로 ocid 검색
     */
    @Transactional
    public Map<String, Object> searchCharacter(String characterName) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. DB에서 먼저 확인
        Optional<MapleCharacter> existingCharacter = characterRepository.findByCharacterName(characterName);
        if (existingCharacter.isPresent()) {
            MapleCharacter character = existingCharacter.get();
            result.put("ocid", character.getOcid());
            result.put("characterName", character.getCharacterName());
            result.put("worldName", character.getWorldName());
            result.put("characterClass", character.getCharacterClass());
            return result;
        }
        
        // 2. API 호출로 ocid 조회
        String ocid = getCharacterOcid(characterName);
        if (ocid == null) {
            result.put("error", "캐릭터를 찾을 수 없습니다");
            return result;
        }
        
        // 기본 정보 반환
        result.put("ocid", ocid);
        result.put("characterName", characterName);
        return result;
    }

    /**
     * ocid로 캐릭터 상세 정보 조회
     */
    @Transactional
    public Map<String, Object> getCharacterInfo(String ocid) {
        // 캐릭터 기본 정보 조회
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String url = baseUrl + "/character/basic?ocid=" + ocid + "&date=" + yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-nxopen-api-key", apiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> result = response.getBody();
        
        if (result == null || result.containsKey("error")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "캐릭터 정보를 찾을 수 없습니다");
            return error;
        }
        
        String characterName = (String) result.get("character_name");
        
        // DB에 캐릭터 정보 저장 또는 업데이트 (계정 ID와 함께)
        if (characterName != null && result.get("world_name") != null && result.get("character_class") != null) {
            Optional<MapleCharacter> existingCharacter = characterRepository.findByOcid(ocid);
            
            // 계정 ID 생성 (캐릭터 이름 기반)
            // 캐릭터 이름과 월드 정보를 기반으로 accountId를 생성합 (generateAccountId 메서드 사용)
            // 새로운 캐릭터 정보를 DB에 저장할 때 accountId도 함께 저장
            // 응답에 accountId를 추가하여 클라이언트에게 반환
            // 이 방식으로 각 캐릭터와 계정 ID 간의 매핑이 데이터베이스에 저장
            String accountId = generateAccountId(characterName, (String) result.get("world_name"));
            
            if (existingCharacter.isPresent()) {
                // 필요하다면 여기에 캐릭터 정보 업데이트 로직 추가
            } else {
                // 새로운 캐릭터 저장
                MapleCharacter character = MapleCharacter.builder()
                        .characterName(characterName)
                        .accountId(accountId)
                        .worldName((String) result.get("world_name"))
                        .characterClass((String) result.get("character_class"))
                        .ocid(ocid)
                        .build();
                
                characterRepository.save(character);
            }
            
            // 결과에 계정 ID 추가
            result.put("accountId", accountId);
        }
        
        return result;
    }
    
    /**
     * 캐릭터명으로 ocid 조회 (내부 사용 메서드)
     */
    private String getCharacterOcid(String characterName) {
        try {
            // API 호출
            String url = baseUrl + "/id?character_name=" + characterName;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-nxopen-api-key", apiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> result = response.getBody();
            
            return result != null ? (String) result.get("ocid") : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 계정 ID 생성 (캐릭터 이름 + 월드 기반)
     * 캐릭터와 계정 매핑
     */
    private String generateAccountId(String characterName, String worldName) {
        // 간단한 예: 캐릭터 이름과 월드를 조합하여 해시코드 생성
        String baseString = characterName + "@" + worldName;
        String accountId = "ACC_" + Math.abs(baseString.hashCode());
        
        return accountId;
    }
}