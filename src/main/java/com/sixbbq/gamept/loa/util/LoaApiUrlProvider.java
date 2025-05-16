package com.sixbbq.gamept.loa.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoaApiUrlProvider {
    @Value("${loa.api.base-url}")
    private String baseUrl;

    public String getCharacterSiblingsUrl(String characterName) {
        return baseUrl + "/characters/" + characterName + "/siblings";
    }


}
