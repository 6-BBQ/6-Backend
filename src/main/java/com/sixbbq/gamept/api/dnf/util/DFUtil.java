package com.sixbbq.gamept.api.dnf.util;

import org.springframework.web.util.UriComponentsBuilder;

public class DFUtil {

    public static final String NEOPLE_API_BASE_URL = "https://api.neople.co.kr/df";
    public static final String CHARACTER_IMAGE_BASE_URL = "https://img-api.neople.co.kr/df";

    // 유틸리티 클래스이므로 private 생성자로 인스턴스화 방지
    private DFUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 캐릭터 검색 API URL 생성
     */
    public static String buildSearchCharacterApiUrl(String serverId, String characterName, String apiKey, String wordType) {
        return UriComponentsBuilder
                .fromUriString(NEOPLE_API_BASE_URL + "/servers/{server}/characters")
                .queryParam("characterName", characterName)
                .queryParam("wordType", wordType) // "match", "full" 등
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId)
                .toUriString();
    }

    /**
     * 캐릭터 상세 정보 API URL 생성
     */
    public static String buildCharacterInfoApiUrl(String serverId, String characterId, String apiKey) {
        return UriComponentsBuilder
                .fromUriString(NEOPLE_API_BASE_URL + "/servers/{server}/characters/{characterId}")
                .queryParam("apikey", apiKey)
                .buildAndExpand(serverId, characterId)
                .toUriString();
    }

    /**
     * 캐릭터 이미지 URL 생성 메서드
     */
    public static String buildCharacterImageUrl(String serverId, String characterId, int zoom) {
        return String.format("%s/servers/%s/characters/%s?zoom=%d", CHARACTER_IMAGE_BASE_URL, serverId, characterId, zoom);
    }
}
