package com.sixbbq.gamept.api.dnf.dto.type;

public enum CharacterDetailType {
    EQUIPMENT("equipment"),
    AVATAR("avatar"),
    CREATURE("creature"),
    FLAG("flag"),
    TALISMAN("talisman");

    private final String value;

    CharacterDetailType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
