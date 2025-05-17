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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapleService {

    private final RestTemplate restTemplate;
    private final MapleCharacterRepository characterRepository;

    @Value("${maplestory.api.key}")
    private String apiKey;
    
    @Value("${maplestory.api.base-url}")
    private String baseUrl;

    @Transactional
    public Map<String, Object> getCharacterInfo(String characterName) {
        // 1. 캐릭터 ID(ocid) 조회
        String ocid = getCharacterOcid(characterName);
        if (ocid == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "캐릭터를 찾을 수 없습니다");
            return error;
        }

        // 2. 캐릭터 기본 정보 조회
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String url = baseUrl + "/character/basic?ocid=" + ocid + "&date=" + yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-nxopen-api-key", apiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> result = response.getBody();
        
        // 3. 계정 ID 결정 및 DB 저장
        String accountId;
        Optional<MapleCharacter> existingCharacter = characterRepository.findByCharacterName(characterName);
        
        if (existingCharacter.isPresent()) {
            accountId = existingCharacter.get().getAccountId();
        } else {
            accountId = result.containsKey("account_id") && result.get("account_id") != null 
                    ? (String) result.get("account_id") 
                    : "account_" + characterName;
            
            // DB에 저장
            if (result.get("world_name") != null && result.get("character_class") != null) {
                MapleCharacter character = MapleCharacter.builder()
                        .characterName(characterName)
                        .accountId(accountId)
                        .worldName((String) result.get("world_name"))
                        .characterClass((String) result.get("character_class"))
                        .ocid(ocid)
                        .build();
                
                characterRepository.save(character);
            }
        }
        
        result.put("accountId", accountId);
        return result;
    }
    
    private String getCharacterOcid(String characterName) {
        try {
            // 1. DB에서 먼저 확인
            Optional<MapleCharacter> existingCharacter = characterRepository.findByCharacterName(characterName);
            if (existingCharacter.isPresent()) {
                return existingCharacter.get().getOcid();
            }

            // 2. API 호출
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

    public List<Map<String, Object>> getAccountCharacters(String accountId) {
        List<MapleCharacter> characters = characterRepository.findByAccountId(accountId);
        
        return characters.stream()
                .map(character -> {
                    Map<String, Object> charMap = new HashMap<>();
                    charMap.put("characterName", character.getCharacterName());
                    charMap.put("worldName", character.getWorldName());
                    charMap.put("characterClass", character.getCharacterClass());
                    charMap.put("ocid", character.getOcid());
                    return charMap;
                })
                .collect(Collectors.toList());
    }
}
