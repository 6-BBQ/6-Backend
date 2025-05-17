package com.sixbbq.gamept.maple.repository;

import com.sixbbq.gamept.maple.entity.MapleCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapleCharacterRepository extends JpaRepository<MapleCharacter, Long> {
    
    Optional<MapleCharacter> findByCharacterName(String characterName);
    
    List<MapleCharacter> findByAccountId(String accountId);
    
    boolean existsByCharacterName(String characterName);
}
