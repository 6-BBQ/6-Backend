package com.sixbbq.gamept.api.dnf.service;

import com.sixbbq.gamept.api.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.api.dnf.entity.DFCharacter;
import com.sixbbq.gamept.api.dnf.repository.DFCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DFCharacterService {

    private final DFCharacterRepository dfCharacterRepository;

    @Transactional
    public void saveOrUpdate(String characterId, String characterName, String serverId, String adventureName) {
        DFCharacter characterToSave = dfCharacterRepository
                .findByCharacterIdAndServerId(characterId, serverId)
                .map(existingCharacter -> {
                    existingCharacter.setCharacterName(characterName);
                    existingCharacter.setAdventureName(adventureName);
                    existingCharacter.setLastUpdated(LocalDateTime.now());
                    return existingCharacter;
                })
                .orElse(DFCharacter.builder()
                        .characterId(characterId)
                        .characterName(characterName)
                        .serverId(serverId)
                        .adventureName(adventureName)
                        .lastUpdated(LocalDateTime.now())
                        .build());

        dfCharacterRepository.save(characterToSave);
    }

    @Transactional(readOnly = true)
    public List<DFCharacterResponseDTO> findByAdventureName(String adventureName) {
        return dfCharacterRepository.findByAdventureName(adventureName)
                .stream()
                .map(dfCharacter -> DFCharacterResponseDTO.builder()
                        .characterId(dfCharacter.getCharacterId())
                        .characterName(dfCharacter.getCharacterName())
                        .serverId(dfCharacter.getServerId())
                        .adventureName(dfCharacter.getAdventureName())
                        .lastUpdated(dfCharacter.getLastUpdated())
                        .build())
                .collect(Collectors.toList());
    }
}
