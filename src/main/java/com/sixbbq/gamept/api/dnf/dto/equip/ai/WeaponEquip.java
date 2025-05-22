package com.sixbbq.gamept.api.dnf.dto.equip.ai;

import com.sixbbq.gamept.api.dnf.dto.equip.Equip;
import com.sixbbq.gamept.api.dnf.dto.equip.FusionOption;
import com.sixbbq.gamept.api.dnf.dto.equip.Tune;
import com.sixbbq.gamept.api.dnf.dto.equip.UpgradeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class WeaponEquip {
    private String slotName;
    private String itemRarity;

    protected WeaponEquip() {
    }

    public WeaponEquip(Equip equip) {
        this.slotName = equip.getSlotName();
        this.itemRarity = equip.getItemRarity();
    }
}
