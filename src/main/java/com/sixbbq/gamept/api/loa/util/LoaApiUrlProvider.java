package com.sixbbq.gamept.api.loa.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public class LoaApiUrlProvider {
    public static String getCharacterRoasterUrl(String baseUrl, String characterName) {
        return baseUrl + "/characters/" + characterName + "/siblings";
    }

    public static String getCharacterInfoUrl(String baseUrl, String characterName, String filter) {
        if(filter == null || filter.isEmpty())

            return String.format("%s/armories/characters/%s", baseUrl, characterName);
        else {
            return String.format("%s/armories/characters/%s/%s", baseUrl, characterName, filter);
        }
    }


}
