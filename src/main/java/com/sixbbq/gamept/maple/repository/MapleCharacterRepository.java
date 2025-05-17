package com.sixbbq.gamept.maple.repository;

import com.sixbbq.gamept.maple.entity.MapleCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MapleCharacterRepository extends JpaRepository<MapleCharacter, Long> {
    
    Optional<MapleCharacter> findByCharacterName(String characterName);
    
    Optional<MapleCharacter> findByOcid(String ocid);
}