package com.sixbbq.gamept.maple.service;

import com.sixbbq.gamept.maple.entity.MapleCharacter;
import com.sixbbq.gamept.maple.model.dto.MapleCharacterResponseDto;
import com.sixbbq.gamept.maple.repository.MapleCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
     * 캐릭터명으로 기본 정보 및 본캐 매핑 포함 정보 조회
     */
    @Transactional
    public Map<String, Object> searchCharacter(String characterName) {
        Map<String, Object> result = new HashMap<>();

        // 1. 캐릭터명으로 ocid 조회
        String ocid = getCharacterOcid(characterName);
        if (ocid == null) {
            result.put("error", "캐릭터를 찾을 수 없습니다");
            return result;
        }

        // 2. ocid로 기본 정보 조회
        Map<String, Object> info = getCharacterInfo(ocid);
        if (info == null || info.get("character_name") == null) {
            result.put("error", "캐릭터 기본 정보를 가져올 수 없습니다");
            return result;
        }

        // 3. 결과 구성
        result.put("ocid", ocid);
        result.put("characterName", info.get("character_name"));
        result.put("worldName", info.get("world_name"));
        result.put("characterLevel", info.get("character_level"));
        result.put("characterClass", info.get("character_class"));
        result.put("characterGender", info.get("character_gender"));
        result.put("characterImage", info.get("character_image"));
        result.put("representativeName", info.get("representative_name"));

        return result;
    }

    /**
     * ocid로 메이플 캐릭터 기본 정보를 조회하고,
     * 해당 캐릭터를 DB에 저장/갱신하며 본캐 기준으로 묶기
     */
    @Transactional
    public Map<String, Object> getCharacterInfo(String ocid) {
        LocalDate date = LocalDate.now().minusDays(1);
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String basicUrl = baseUrl + "/character/basic?ocid=" + ocid + "&date=" + dateStr;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-nxopen-api-key", apiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // 1. 기본 정보 API 호출
        ResponseEntity<Map> response = restTemplate.exchange(basicUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> result = response.getBody();

        if (result == null || result.containsKey("error")) {
            return null;
        }

        // 2. 캐릭터 정보 파싱
        String characterName = (String) result.get("character_name");
        String worldName = (String) result.get("world_name");

        // 3. 대표 캐릭터명 조회
        String representativeName = getRepresentativeName(worldName, ocid, dateStr);
        result.put("representative_name", representativeName);

        // 4. DB 저장 or 업데이트
        Optional<MapleCharacter> existing = characterRepository.findByOcid(ocid);

        if (existing.isPresent()) {
            MapleCharacter character = existing.get();
            if (!representativeName.equals(character.getRepresentativeName())) {
                character.setRepresentativeName(representativeName);
                characterRepository.save(character);
            }
        } else {
            MapleCharacter character = MapleCharacter.builder()
                    .characterName(characterName)
                    .worldName(worldName)
                    .ocid(ocid)
                    .characterClass((String) result.get("character_class"))
                    .representativeName(representativeName)
                    .build();
            characterRepository.save(character);
        }

        return result;
    }

    /**
     * 캐릭터명으로 ocid 조회
     */
    private String getCharacterOcid(String characterName) {
        try {
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
     * 랭킹 API를 통해 대표 캐릭터명(본캐) 추출
     */
    private String getRepresentativeName(String worldName, String ocid, String dateStr) {
        try {
            String url = baseUrl + "/ranking/union"
                    + "?date=" + dateStr
                    + "&ocid=" + ocid
                    + "&world_name=" + URLEncoder.encode(worldName, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-nxopen-api-key", apiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("ranking")) return characterNameFallback(ocid); // fallback

            List<Map<String, Object>> rankings = (List<Map<String, Object>>) body.get("ranking");
            if (rankings.isEmpty()) return characterNameFallback(ocid);

            return (String) rankings.get(0).get("character_name");
        } catch (Exception e) {
            return characterNameFallback(ocid); // fallback
        }
    }

    /**
     * 대표 캐릭터명 기준으로 묶인 캐릭터 목록을 DTO로 반환
     */
     public List<MapleCharacterResponseDto> findCharactersByRepresentative(String representativeName) {
        return characterRepository.findByRepresentativeName(representativeName).stream()
                .map(c -> MapleCharacterResponseDto.builder()
                        .characterName(c.getCharacterName())
                        .worldName(c.getWorldName())
                        .characterClass(c.getCharacterClass())
                        .ocid(c.getOcid())
                        .representativeName(c.getRepresentativeName())
                        .build())
                .toList();
    }

    /**
     * 랭킹 API 실패 시 fallback: 현재 캐릭터 이름 반환
     */
    private String characterNameFallback(String ocid) {
        Optional<MapleCharacter> optional = characterRepository.findByOcid(ocid);
        return optional.map(MapleCharacter::getCharacterName).orElse("UNKNOWN");
    }
}