package com.sixbbq.gamept.dnf.service;

import com.sixbbq.gamept.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.dnf.util.DFUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DFService {

    private final RestTemplate restTemplate;
    private final DFCharacterService dfCharacterService;

    @Value("${dnf.api.key}")
    private String apiKey;

    private static final String WORD_TYPE_MATCH = "match";

    /**
     * 컨트롤러로부터 검색 요청을 받아 분기 처리하는 메서드
     */
    public Map<String, Object> processSearchRequest(String serverIdParam, String nameParam) {
        if ("adven".equalsIgnoreCase(serverIdParam)) {
            return searchCharactersByAdventureName(nameParam); // 모험단명으로 검색
        } else {
            return searchCharacterByServerAndName(serverIdParam, nameParam); // 서버ID와 캐릭터명으로 검색
        }
    }

    /**
     * 모험단명으로 캐릭터 목록 검색
     */
    private Map<String, Object> searchCharactersByAdventureName(String adventureName) {
        List<DFCharacterResponseDTO> membersInDB = dfCharacterService.findByAdventureName(adventureName);
        List<Map<String, Object>> foundCharactersFromApi = new ArrayList<>();

        for (DFCharacterResponseDTO member : membersInDB) {
            String memberServerId = member.getServerId();
            String memberCharacterName = member.getCharacterName();

            String apiUrl = DFUtil.buildSearchCharacterApiUrl(memberServerId, memberCharacterName, apiKey, WORD_TYPE_MATCH);

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> apiResponseData = restTemplate.getForObject(apiUrl, Map.class);

                if (apiResponseData != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> rows = (List<Map<String, Object>>) apiResponseData.get("rows");

                    if (rows != null && !rows.isEmpty()) {
                        for (Map<String, Object> apiCharacterInfo : rows) {
                            String apiCharacterId = (String) apiCharacterInfo.get("characterId");
                            String apiServerId = (String) apiCharacterInfo.get("serverId");

                            if (member.getCharacterId().equals(apiCharacterId) && member.getServerId().equals(apiServerId)) {
                                String imageUrl = DFUtil.buildCharacterImageUrl(apiServerId, apiCharacterId, 1);
                                apiCharacterInfo.put("imageUrl", imageUrl);
                                foundCharactersFromApi.add(apiCharacterInfo);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("모험단 내 캐릭터 '" + memberCharacterName + "'(" + memberServerId + ") 조회 실패: " + e.getMessage());
                return Map.of("rows", Collections.emptyList(), "error", "캐릭터 검색 중 오류가 발생했습니다.");
            }
        }
        return Map.of("rows", foundCharactersFromApi);
    }

    /**
     * 서버ID와 캐릭터명으로 캐릭터 검색
     */
    private Map<String, Object> searchCharacterByServerAndName(String serverId, String characterName) {
        String apiUrl = DFUtil.buildSearchCharacterApiUrl(serverId, characterName, apiKey, WORD_TYPE_MATCH);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> apiResponseData = responseEntity.getBody();

            if (apiResponseData != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rows = (List<Map<String, Object>>) apiResponseData.get("rows");
                if (rows != null && !rows.isEmpty()) {
                    for (Map<String, Object> apiCharacterInfo : rows) {
                        String charId = (String) apiCharacterInfo.get("characterId");
                        String charServerId = (String) apiCharacterInfo.get("serverId");
                        String imageUrl = DFUtil.buildCharacterImageUrl(charServerId, charId, 1);
                        apiCharacterInfo.put("imageUrl", imageUrl);
                    }
                }
                return apiResponseData;
            }
            return Map.of("rows", Collections.emptyList());
        } catch (Exception e) {
            System.err.println("캐릭터 검색 실패 ("+ serverId +", "+ characterName +"): " + e.getMessage());
            return Map.of("rows", Collections.emptyList(), "error", "캐릭터 검색 중 오류가 발생했습니다.");
        }
    }

    /**
     * 캐릭터 상세 정보 조회
     */
    public Map<String, Object> getCharacterInfo(String serverId, String characterId) {
        String apiUrl = DFUtil.buildCharacterInfoApiUrl(serverId, characterId, apiKey);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> characterDetails = responseEntity.getBody();

            if (characterDetails != null) {
                String charName = (String) characterDetails.get("characterName");
                String advName = (String) characterDetails.get("adventureName");
                String apiServerId = (String) characterDetails.get("serverId"); // API 응답의 서버 ID 사용

                if (charName != null && advName != null && apiServerId != null) {
                    // DB 저장 시 API 응답에서 받은 serverId를 사용하는 것이 더 정확할 수 있음
                    dfCharacterService.saveOrUpdate(characterId, charName, apiServerId, advName);
                }

                String imageUrl = DFUtil.buildCharacterImageUrl(apiServerId, characterId, 2);
                characterDetails.put("imageUrl", imageUrl);

                return characterDetails;
            }
            return Collections.emptyMap();
        } catch (Exception e) {
            System.err.println("캐릭터 상세 정보 조회 실패 ("+ serverId +", "+ characterId +"): " + e.getMessage());
            return Map.of("error", "캐릭터 상세 정보 조회 중 오류가 발생했습니다.");
        }
    }
}
