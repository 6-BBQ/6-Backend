package com.sixbbq.gamept.loa.service;

import com.sixbbq.gamept.loa.model.dto.loaapi.response.CharacterInfoResponseDTO;
import com.sixbbq.gamept.loa.util.LoaApiUrlProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

@Service
public class LoaService {
    @Value("${lostark.api.token}")
    private String token;

    private final RestTemplate restTemplate;
    private final LoaApiUrlProvider urlProvider;

    public LoaService(RestTemplate restTemplate, LoaApiUrlProvider urlProvider) {
        this.restTemplate = restTemplate;
        this.urlProvider = urlProvider;
    }

    public List<CharacterInfoResponseDTO> getCharacters(String characterName) {
        String url = urlProvider.getCharacterSiblingsUrl(characterName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "bearer " + token);
        headers.set("Accept", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<CharacterInfoResponseDTO[]> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, CharacterInfoResponseDTO[].class
        );

        if (response.getBody() != null)
            return Arrays.asList(response.getBody());
        else
            return null;
    }
}
