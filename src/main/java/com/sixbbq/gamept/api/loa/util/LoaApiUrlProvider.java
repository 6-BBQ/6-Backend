package com.sixbbq.gamept.api.loa.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoaApiUrlProvider {
    @Value("${loa.api.base-url}")
    private String baseUrl;

    public String getCharacterRoasterUrl(String characterName) {
        return baseUrl + "/characters/" + characterName + "/siblings";
    }

    public String getCharacterInfoUrl(String characterName, String filter) {
        if(filter == null || filter.isEmpty())
            return baseUrl + "/armories/characters/" + characterName;
        else {
            return baseUrl + "/armories/characters/" + characterName + "/" + filter;
        }
    }


}
