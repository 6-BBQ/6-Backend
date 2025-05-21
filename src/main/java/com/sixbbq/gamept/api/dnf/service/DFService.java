package com.sixbbq.gamept.api.dnf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.api.dnf.dto.buff.buffAvatar.BuffAvatar;
import com.sixbbq.gamept.api.dnf.dto.buff.buffCreature.BuffCreature;
import com.sixbbq.gamept.api.dnf.dto.creature.Creature;
import com.sixbbq.gamept.api.dnf.dto.equip.Equip;
import com.sixbbq.gamept.api.dnf.dto.flag.Flag;
import com.sixbbq.gamept.api.dnf.dto.buff.buffEquip.BuffSkill;
import com.sixbbq.gamept.api.dnf.dto.skill.Skill;
import com.sixbbq.gamept.api.dnf.dto.type.CharacterDetailType;
import com.sixbbq.gamept.api.dnf.util.DFUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DFService {

    private static final Logger log = LoggerFactory.getLogger(DFService.class);
    private final RestTemplate restTemplate;
    private final DFCharacterService dfCharacterService;

    @Value("${dnf.api.key}")
    private String apiKey;
    @Value("${dnf.api.base-url}")
    private String NEOPLE_API_BASE_URL;
    @Value("${dnf.api.character-image-base-url}")
    private String CHARACTER_IMAGE_BASE_URL;
    @Value("${dnf.api.item-image-base-url}")
    private String ITEM_IMAGE_BASE_URL;

    private final String WORD_TYPE_MATCH = "match";

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

            String apiUrl = DFUtil.buildSearchCharacterApiUrl(NEOPLE_API_BASE_URL, memberServerId, memberCharacterName, apiKey, WORD_TYPE_MATCH);

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
                                String imageUrl = DFUtil.buildCharacterImageUrl(CHARACTER_IMAGE_BASE_URL, apiServerId, apiCharacterId, 1);
                                apiCharacterInfo.put("imageUrl", imageUrl);
                                foundCharactersFromApi.add(apiCharacterInfo);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("모험단 내 캐릭터 '" + memberCharacterName + "'(" + memberServerId + ") 조회 실패: " + e.getMessage());
                throw new NoSuchElementException("캐릭터 검색 중 오류가 발생했습니다.");
            }
        }
        return Map.of("rows", foundCharactersFromApi);
    }

    /**
     * 서버ID와 캐릭터명으로 캐릭터 검색
     */
    private Map<String, Object> searchCharacterByServerAndName(String serverId, String characterName) {
        String apiUrl = DFUtil.buildSearchCharacterApiUrl(NEOPLE_API_BASE_URL, serverId, characterName, apiKey, WORD_TYPE_MATCH);

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
                        String imageUrl = DFUtil.buildCharacterImageUrl(CHARACTER_IMAGE_BASE_URL, charServerId, charId, 1);
                        apiCharacterInfo.put("imageUrl", imageUrl);
                    }
                }
                return apiResponseData;
            }
            return Map.of("rows", Collections.emptyList());
        } catch (Exception e) {
            log.error("캐릭터 검색 실패 ("+ serverId +", "+ characterName +"): " + e.getMessage());
            throw new NoSuchElementException("캐릭터 검색 중 오류가 발생했습니다.");
        }
    }

    /**
     * 캐릭터 상세 정보 조회
     */
    public DFCharacterResponseDTO getCharacterInfo(String serverId, String characterId) {
        try {
            String apiUrl = DFUtil.buildCharacterStatusApiUrl(NEOPLE_API_BASE_URL, serverId, characterId, apiKey);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> characterDetails = responseEntity.getBody();
            DFCharacterResponseDTO dto = null;

            if (characterDetails != null) {
                ObjectMapper objectMapper = new ObjectMapper();

                dto = objectMapper.convertValue(characterDetails, DFCharacterResponseDTO.class);

                if (dto.getCharacterName() != null && dto.getAdventureName() != null && dto.getServerId() != null) {
                    dfCharacterService.saveOrUpdate(characterId, dto);
                }

                for (CharacterDetailType type : CharacterDetailType.values()) {
                    if (type == CharacterDetailType.SKILL) {
                        apiUrl = DFUtil.buildCharacterSkillStyleApiUrl(NEOPLE_API_BASE_URL, serverId, characterId, apiKey);
                    } else if (type == CharacterDetailType.BUFF_EQUIPMENT) {
                        apiUrl = DFUtil.buildCharacterBuffInfoApiUrl(NEOPLE_API_BASE_URL, serverId, characterId, apiKey, "equipment");
                    } else if (type == CharacterDetailType.BUFF_AVATAR) {
                        apiUrl = DFUtil.buildCharacterBuffInfoApiUrl(NEOPLE_API_BASE_URL, serverId, characterId, apiKey, "avatar");
                    } else if (type == CharacterDetailType.BUFF_CREATURE) {
                        apiUrl = DFUtil.buildCharacterBuffInfoApiUrl(NEOPLE_API_BASE_URL, serverId, characterId, apiKey, "creature");
                    } else {
                        apiUrl = DFUtil.buildCharacterDetailInfoApiUrl(NEOPLE_API_BASE_URL, serverId, characterId, apiKey, type.getValue());
                    }
                    responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, Map.class);

                    characterDetails = responseEntity.getBody();

                    switch (type) {
                        case EQUIPMENT:
                            dto.setEquipment(objectMapper.convertValue(characterDetails.get("equipment"), new TypeReference<>() {}));
                            dto.setSetItemInfo(objectMapper.convertValue(characterDetails.get("setItemInfo"), new TypeReference<>() {}));
                            for(Equip equip : dto.getEquipment()) {
                                equip.setItemImage(DFUtil.buildEquipmentImageUrl(ITEM_IMAGE_BASE_URL, equip.getItemId()));
                            }
                            break;
                        case AVATAR:
                            dto.setAvatar(objectMapper.convertValue(characterDetails.get("avatar"), new TypeReference<>() {}));
                            break;
                        case CREATURE:
                            dto.setCreature(objectMapper.convertValue(characterDetails.get("creature"), Creature.class));
                            break;
                        case FLAG:
                            dto.setFlag(objectMapper.convertValue(characterDetails.get("flag"), Flag.class));
                            break;
                        case TALISMAN:
                            dto.setTalismans(objectMapper.convertValue(characterDetails.get("talismans"), new TypeReference<>() {}));
                            break;
                        case SKILL:
                            dto.setSkill(objectMapper.convertValue(characterDetails.get("skill"), Skill.class));
                            // 레벨1인 스킬은 제외하는 부분. 던담 스타일로 갈거면 필요없는 데이터를 걸러야 하므로 사용
//                            dto.getSkill().getStyle().setActive(
//                                    dto.getSkill().getStyle().getActive().stream()
//                                    .filter(skill -> skill.getLevel() != null && skill.getLevel() != 1)
//                                    .collect(Collectors.toList())
//                            );
//                            dto.getSkill().getStyle().setPassive(
//                                    dto.getSkill().getStyle().getPassive().stream()
//                                            .filter(skill -> skill.getLevel() != null && skill.getLevel() != 1)
//                                            .collect(Collectors.toList())
//                            );
                            break;
                        case BUFF_EQUIPMENT:
                            if (dto.getSkill() == null) dto.setSkill(new Skill());
                            dto.getSkill().setBuff(objectMapper.convertValue(
                                    ((Map<?, ?>) ((Map<?, ?>) characterDetails.get("skill")).get("buff")), BuffSkill.class));
                            break;
                        case BUFF_AVATAR:
                            if (dto.getSkill() == null) dto.setSkill(new Skill());
                            Map<String, Object> buffMapAvatar = (Map<String, Object>) ((Map<?, ?>) characterDetails.get("skill")).get("buff");
                            if (dto.getSkill().getBuff() == null) {
                                dto.getSkill().setBuff(objectMapper.convertValue(buffMapAvatar, BuffSkill.class));
                            } else {
                                dto.getSkill().getBuff().setAvatar(
                                        objectMapper.convertValue(buffMapAvatar.get("avatar"), new TypeReference<List<BuffAvatar>>() {}));
                            }
                            break;
                        case BUFF_CREATURE:
                            if (dto.getSkill() == null) dto.setSkill(new Skill());
                            Map<String, Object> buffMapCreature = (Map<String, Object>) ((Map<?, ?>) characterDetails.get("skill")).get("buff");
                            if (dto.getSkill().getBuff() == null) {
                                dto.getSkill().setBuff(objectMapper.convertValue(buffMapCreature, BuffSkill.class));
                            } else {
                                dto.getSkill().getBuff().setCreature(
                                        objectMapper.convertValue(buffMapCreature.get("creature"), new TypeReference<List<BuffCreature>>() {}));
                            }
                            break;

                    }
                }
                
                String imageUrl = DFUtil.buildCharacterImageUrl(CHARACTER_IMAGE_BASE_URL, dto.getServerId(), characterId, 2);
                dto.setImageUrl(imageUrl);
                characterDetails.put("imageUrl", imageUrl);
            }

            return dto;
        } catch (Exception e) {
            log.error("캐릭터 상세 정보 조회 실패 ("+ serverId +", "+ characterId +"): " + e.getMessage());
            throw new NoSuchElementException("캐릭터 상세 정보 조회 중 오류가 발생했습니다.");
        }
    }


}