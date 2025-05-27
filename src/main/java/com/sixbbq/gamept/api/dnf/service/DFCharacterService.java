package com.sixbbq.gamept.api.dnf.service;

import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.characterRegist.repository.CharacterRegistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DFCharacterService {

    private final CharacterRegistRepository characterRegistRepository;

    @Transactional(readOnly = true)
    public List<DFCharacterResponseDTO> findByAdventureName(String adventureName) {
        return characterRegistRepository.findByAdventureName(adventureName)
                .stream()
                .map(dfCharacter -> DFCharacterResponseDTO.builder()
                        .characterId(dfCharacter.getCharacterId())
                        .characterName(dfCharacter.getCharacterName())
                        .serverId(dfCharacter.getServerId())
                        .adventureName(dfCharacter.getAdventureName())
                        .build())
                .collect(Collectors.toList());
    }
}
