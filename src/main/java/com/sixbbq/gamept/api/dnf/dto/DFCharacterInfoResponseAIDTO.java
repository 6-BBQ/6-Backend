package com.sixbbq.gamept.api.dnf.dto;

import com.sixbbq.gamept.api.dnf.dto.avatar.Avatar;
import com.sixbbq.gamept.api.dnf.dto.equip.Equip;
import com.sixbbq.gamept.api.dnf.dto.equip.ai.SetItemInfoAI;
import com.sixbbq.gamept.api.dnf.dto.equip.ai.WeaponEquip;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@ToString
@AllArgsConstructor
// 캐릭터의 요약 정보[AI한테 데이터 보내주기 용]
public class DFCharacterInfoResponseAIDTO {
    private String jobGrowName;
    private String jobName;
    private String fame;
    private WeaponEquip weaponEquip;
    private Integer epicNum;
    private Integer originalityNum;
    private String titleName;
    private List<SetItemInfoAI> setItemInfoAI;
    private String creatureName;
    private String auraName;

    public DFCharacterInfoResponseAIDTO(DFCharacterResponseDTO dto) {
        this.jobGrowName = dto.getJobGrowName();
        this.jobName = dto.getJobName();
        this.fame = dto.getFame();
        this.weaponEquip = new WeaponEquip(
                dto.getEquipment().stream()
                        // 무기를 발견하지 못한다면 빈 Equip을 반환
                .filter(equip -> equip.getSlotName().equals("무기"))
                        .findFirst().orElse(new Equip()));
        this.epicNum = Math.toIntExact(dto.getEquipment().stream()
                .filter(equip -> !equip.getSlotName().equals("무기") &&
                        !equip.getSlotName().equals("보조장비")) // 무기와 보조장비 제외
                .filter(equip -> equip.getItemRarity().equals("에픽")) // 에픽 rarity
                .count());
        this.originalityNum = Math.toIntExact(dto.getEquipment().stream()
                .filter(equip -> !equip.getSlotName().equals("무기") &&
                        !equip.getSlotName().equals("보조장비")) // 무기 제외
                .filter(equip -> equip.getItemRarity().equals("태초")) // 태초 rarity
                .count());
        this.titleName = dto.getEquipment().stream()
                .filter(equip -> equip.getSlotName().equals("칭호"))
                .findFirst()
                .map(Equip::getItemName) // 찾았을 경우 getItemName 호출
                .orElse("칭호 없음"); // 찾지 못했을 경우 안전한 기본값 설정
        this.setItemInfoAI = dto.getSetItemInfo().stream()
                .map(SetItemInfoAI::new) // SetItemInfo를 SetItemInfoAI로 변환
                .collect(Collectors.toList()); // List로 수집
        this.setCreatureName(dto.getCreature().getItemName());
        this.setAuraName(dto.getAvatar().stream()
                .filter(avatar -> avatar.getSlotName().equals("오라 아바타"))
                .findFirst()
                .map(Avatar::getItemName)
                .orElse("오라 없음"));
    }
}
