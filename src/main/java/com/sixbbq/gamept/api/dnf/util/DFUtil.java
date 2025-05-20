package com.sixbbq.gamept.api.dnf.util;

import org.springframework.web.util.UriComponentsBuilder;

public class DFUtil {
    // 유틸리티 클래스이므로 private 생성자로 인스턴스화 방지
    private DFUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 캐릭터 검색 API URL 생성
     */
    public static String buildSearchCharacterApiUrl(String baseUrl, String serverId, String characterName, String apiKey, String wordType) {
        return UriComponentsBuilder
                .fromUriString(baseUrl + "/servers/{server}/characters")
                .queryParam("characterName", characterName)
                .queryParam("wordType", wordType) // "match", "full" 등
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId)
                .toUriString();
    }

    /**
     * 캐릭터 상세 정보 API URL 생성
     */
    public static String buildCharacterInfoApiUrl(String baseUrl, String serverId, String characterId, String apiKey) {
        return UriComponentsBuilder
                .fromUriString(baseUrl + "/servers/{server}/characters/{characterId}")
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId, characterId)
                .toUriString();
    }

    /**
     * 캐릭터 이미지 URL 생성 메서드
     */
    public static String buildCharacterImageUrl(String baseUrl, String serverId, String characterId, int zoom) {
        return String.format("%s/servers/%s/characters/%s?zoom=%d", baseUrl, serverId, characterId, zoom);
    }

    /**
     * 캐릭터 능력치 API URL 생성
     */
    public static String buildCharacterStatusApiUrl(String baseUrl, String serverId, String characterId, String apiKey) {
        return UriComponentsBuilder
                .fromUriString(baseUrl + "/servers/{server}/characters/{characterId}/status")
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId, characterId)
                .toUriString();
    }

    /**
     * 캐릭터 장착 장비 API URL 생성
     * getInfoUrl 목록 : equipment, avatar, crature, flag, talisman
     */
    public static String buildCharacterDetailInfoApiUrl(String baseUrl, String serverId, String characterId, String apiKey, String getInfoUrl) {
        return UriComponentsBuilder
                .fromUriString(baseUrl + "/servers/{server}/characters/{characterId}/equip/{getInfoUrl}")
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId, characterId, getInfoUrl)
                .toUriString();
    }

    /**
     * 캐릭터 스킬 스타일 API URL 생성
     */
    public static String buildCharacterSkillStyleApiUrl(String baseUrl, String serverId, String characterId, String apiKey) {
        return UriComponentsBuilder
                .fromUriString(baseUrl + "/servers/{server}/characters/{characterId}/skill/style")
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId, characterId)
                .toUriString();
    }

    /**
     * 캐릭터 버프 스킬 강화 API URL 생성
     * getInfoUrl 목록 : equipment, avatar, creature
     */

    public static String buildCharacterBuffInfoApiUrl(String baseUrl, String serverId, String characterId, String apiKey, String getInfoUrl) {
        return UriComponentsBuilder
                .fromUriString(baseUrl + "/servers/{server}/characters/{characterId}/skill/buff/equip/{getInfoUrl}")
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId, characterId, getInfoUrl)
                .toUriString();
    }
}
