package com.sixbbq.gamept.api.dnf.dto.type;

public enum CharacterDetailType {
    EQUIPMENT("equipment"),
    AVATAR("avatar"),
    CREATURE("creature"),
    FLAG("flag"),
//    TALISMAN("talisman"),
    SKILL("skill/style"),
    BUFF_EQUIPMENT("skill/buff/equip/equipment"),
    BUFF_AVATAR("skill/buff/equip/avatar"),
    BUFF_CREATURE("skill/buff/equip/creature");

    private final String value;

    CharacterDetailType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
