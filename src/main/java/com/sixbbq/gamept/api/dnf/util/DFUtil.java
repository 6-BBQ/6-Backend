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
}
