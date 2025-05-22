package com.sixbbq.gamept.api.dnf.dto.equip.ai;

import com.sixbbq.gamept.api.dnf.dto.equip.SetItemInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SetItemInfoAI {
    private String setItemName;
    private String setItemRarityName;

    protected SetItemInfoAI() {}

    public SetItemInfoAI(SetItemInfo setItemInfo) {
        this.setItemName = setItemInfo.getSetItemName();
        this.setItemRarityName = setItemInfo.getSetItemRarityName();
    }
}
