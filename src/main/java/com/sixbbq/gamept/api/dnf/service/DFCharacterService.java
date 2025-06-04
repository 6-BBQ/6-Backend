package com.sixbbq.gamept.api.dnf.service;

import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.characterRegist.repository.CharacterRegistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class DFCharacterService {

//    private final DFService dfService;
    private final CharacterRegistRepository characterRegistRepository;

    @Transactional(readOnly = true)
    public List<DFCharacterResponseDTO> findByAdventureName(String adventureName) {
        return characterRegistRepository.findDistinctCharacterIdByAdventureName(adventureName)
                .stream()
                .map(dfCharacter -> DFCharacterResponseDTO.builder()
                        .characterId(dfCharacter.getCharacterId())
                        .characterName(dfCharacter.getCharacterName())
                        .serverId(dfCharacter.getServerId())
                        .adventureName(dfCharacter.getAdventureName())
                        .build())
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public DFCharacterResponseDTO getOtherCharacterInfo(String serverId, String characterId) {
//        // DFService의 getCharacterInfo를 사용하여 캐릭터 정보와 경매장 정보를 함께 조회
//        return dfService.getCharacterInfo(serverId, characterId);
//    }
}
