package com.sixbbq.gamept.api.maple.repository;

import com.sixbbq.gamept.api.maple.entity.MapleCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapleCharacterRepository extends JpaRepository<MapleCharacter, Long> {
    
    Optional<MapleCharacter> findByOcid(String ocid);

    List<MapleCharacter> findByRepresentativeName(String representativeName);
}