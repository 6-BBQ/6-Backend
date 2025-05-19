package com.sixbbq.gamept.api.loa.service;

import com.sixbbq.gamept.api.loa.model.dto.loaapi.response.CharacterInfoResponseDTO;
import com.sixbbq.gamept.api.loa.util.LoaApiUrlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class LoaService {
    @Value("${loa.api.token}")
    private String token;

    private final RestTemplate restTemplate;
    private final LoaApiUrlProvider urlProvider;

    public LoaService(RestTemplate restTemplate, LoaApiUrlProvider urlProvider) {
        this.restTemplate = restTemplate;
        this.urlProvider = urlProvider;
    }

    public List<CharacterInfoResponseDTO> getRoster(String characterName) {
        String url = urlProvider.getCharacterRoasterUrl(characterName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "bearer " + token);
        headers.set("Accept", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CharacterInfoResponseDTO[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, CharacterInfoResponseDTO[].class
        );

        if (response.getBody() != null) {
            List<CharacterInfoResponseDTO> characterList = Arrays.asList(response.getBody());
            // itemMaxLevel이 높은 순으로 재정렬
            characterList.sort(Comparator.comparingDouble((CharacterInfoResponseDTO c) ->
                    Double.parseDouble(c.getItemMaxLevel().replace(",", ""))
            ).reversed());

            return characterList;
        }
        else
            return null;
    }

    public Map<String, Object> getCharacterInfo(String characterName, String filter) {
        String url = urlProvider.getCharacterInfoUrl(characterName, filter);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "bearer " + token);
        headers.set("Accept", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class
        );

        if(response.getBody() == null)
            return null;

        Map<String, Object> result = response.getBody();

        return result;
    }
}
