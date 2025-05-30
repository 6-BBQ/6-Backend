package com.sixbbq.gamept.api.dnf.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DFCharacterAuctionResponseDTO {
    private Long creaturePrice;  // 크리쳐 가격
    private Long titlePrice;     // 칭호 가격
    private Long auraPrice;      // 오라 가격
}