package com.sixbbq.gamept.dnf.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DFService {

    private final RestTemplate restTemplate;

    @Value("${dnf.api.key}")
    private String apiKey;

    public Map<String, Object> getCharacterBasicInfo(String server, String name) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://api.neople.co.kr/df/servers/{server}/characters")
                    .queryParam("characterName", name)
                    .queryParam("apikey", apiKey)
                    .buildAndExpand(server)
                    .toUriString();

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("던파 캐릭터 조회 실패: " + e.getMessage());
        }
    }

}
