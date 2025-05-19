package com.sixbbq.gamept.api.dnf.repository;

import com.sixbbq.gamept.api.dnf.entity.DFCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DFCharacterRepository extends JpaRepository<DFCharacter, Long> {
    Optional<DFCharacter> findByCharacterIdAndServerId(String characterId, String serverId);
    List<DFCharacter> findByAdventureName(String adventureName);
}
