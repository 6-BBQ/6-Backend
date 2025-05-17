package com.sixbbq.gamept.dnf.service;

import com.sixbbq.gamept.dnf.dto.DFCharacterResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DFService {

    private final RestTemplate restTemplate;
    private final DFCharacterService dfCharacterService;

    @Value("${dnf.api.key}")
    private String apiKey;

    /**
     * 캐릭터 이미지 URL 생성 메서드
     * @param server 서버 ID
     * @param characterId 캐릭터 ID
     * @param zoom 이미지 확대 수준 (1~3)
     * @return 이미지 URL 문자열
     */
    private String buildImageUrl(String server, String characterId, int zoom) {
        return String.format("https://img-api.neople.co.kr/df/servers/%s/characters/%s?zoom=%d", server, characterId, zoom);
    }

    public Map<String, Object> searchCharacter(String server, String name) {
        try {

            // 1. server가 "adven"인 경우: 모험단명 기반 DB 검색
            if ("adven".equalsIgnoreCase(server)) {
                List<DFCharacterResponseDTO> members = dfCharacterService.findByAdventureName(name);
                List<Map<String, Object>> allRows = new ArrayList<>();

                for (DFCharacterResponseDTO member : members) {
                    String sId = member.getServerId();
                    String cName = member.getCharacterName();

                    String url = UriComponentsBuilder
                            .fromUriString("https://api.neople.co.kr/df/servers/{server}/characters")
                            .queryParam("characterName", cName)
                            .queryParam("apikey", apiKey)
                            .buildAndExpand(sId)
                            .toUriString();

                    Map<String, Object> result = restTemplate.getForObject(url, Map.class);
                    List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");

                    if (rows != null && !rows.isEmpty()) {
                        Map<String, Object> character = rows.get(0);

                        String characterId = (String) character.get("characterId");
                        String serverId = (String) character.get("serverId");

                        String imageUrl = buildImageUrl(serverId, characterId, 1);
                        character.put("imageUrl", imageUrl);

                        allRows.add(character);
                    }
                }

                return Map.of("rows", allRows);
            }

            // 2. 일반 검색 (server + characterName)
            String url = UriComponentsBuilder
                    .fromUriString("https://api.neople.co.kr/df/servers/{server}/characters")
                    .queryParam("characterName", name)
                    .queryParam("apikey", apiKey)
                    .buildAndExpand(server)
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
            Map<String, Object> result = response.getBody();

            List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
            if (rows != null && !rows.isEmpty()) {
                for (Map<String, Object> character : rows) {
                    String characterId = (String) character.get("characterId");
                    String serverId = (String) character.get("serverId");

                    String imageUrl = buildImageUrl(serverId, characterId, 1);
                    character.put("imageUrl", imageUrl);
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("던파 캐릭터 조회 실패: " + e.getMessage());
        }
    }

    public Map<String, Object> getCharacterInfo(String server, String characterId) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://api.neople.co.kr/df/servers/{server}/characters/{characterId}")
                    .queryParam("apikey", apiKey)
                    .buildAndExpand(server, characterId)
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
            Map<String, Object> result = response.getBody();
            if (result != null) {
                String characterName = (String) result.get("characterName");
                String adventureName = (String) result.get("adventureName");
                dfCharacterService.saveOrUpdate(characterId, characterName, server, adventureName);

                String imageUrl = buildImageUrl(server, characterId, 2);
                result.put("imageUrl", imageUrl);
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("던파 캐릭터 조회 실패: " + e.getMessage());
        }
    }

}
