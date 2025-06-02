package com.sixbbq.gamept.api.dnf.dto.response;

import com.sixbbq.gamept.api.dnf.dto.DFCharacterAuctionResponseDTO;
import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CurrentCharacterResponseDTO {
    private DFCharacterAuctionResponseDTO characterAuction;
    private DFCharacterResponseDTO character;

    public CurrentCharacterResponseDTO(DFCharacterResponseDTO character, DFCharacterAuctionResponseDTO characterAuction) {
        this.character = character;
        this.characterAuction = characterAuction;
    }
}

