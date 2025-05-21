package com.sixbbq.gamept.characterRegist.repository;

import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRegistRepository extends JpaRepository<CharacterRegist, Long> {
    List<CharacterRegist> findByUserId(String userId);
    boolean existsByUserIdAndCharacterId(String userId, String characterId);
    Optional<CharacterRegist> findByUserIdAndCharacterId(String userId, String characterId);
    boolean existsByCharacterId(String characterId);
    List<CharacterRegist> findByUserIdAndAdventureName(String userId, String adventureName);
}
