package com.sixbbq.gamept.dnf.service;

import com.sixbbq.gamept.dnf.dto.DFCharacterResponseDTO;
import com.sixbbq.gamept.dnf.entity.DFCharacter;
import com.sixbbq.gamept.dnf.repository.DFCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DFCharacterService {

    private final DFCharacterRepository dfCharacterRepository;

    @Transactional
    public void saveOrUpdate(String characterId, String characterName, String serverId, String adventureName) {
        DFCharacter dfCharacter = dfCharacterRepository
                .findByCharacterIdAndServerId(characterId, serverId)
                .map(existing -> {
                    existing.setCharacterName(characterName);
                    existing.setAdventureName(adventureName);
                    existing.setLastUpdated(LocalDateTime.now());
                    return existing;
                })
                .orElse(DFCharacter.builder()
                        .characterId(characterId)
                        .characterName(characterName)
                        .serverId(serverId)
                        .adventureName(adventureName)
                        .lastUpdated(LocalDateTime.now())
                        .build());

        dfCharacterRepository.save(dfCharacter);
    }

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
                .toList();
    }
}
