package com.sixbbq.gamept.characterRegist.repository;

import com.sixbbq.gamept.characterRegist.entity.CharacterRegist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRegistRepository extends JpaRepository<CharacterRegist, Long> {
    List<CharacterRegist> findByUserId(String userId);
    @Query(value = """
    SELECT * FROM characters c
    WHERE c.adventure_name = :adventureName
      AND c.id IN (
          SELECT MIN(id) FROM characters
          WHERE adventure_name = :adventureName
          GROUP BY character_id
      )
          """, nativeQuery = true)
    List<CharacterRegist> findDistinctCharacterIdByAdventureName(@Param("adventureName") String adventureName);

    boolean existsByUserIdAndCharacterId(String userId, String characterId);
    Optional<CharacterRegist> findByUserIdAndCharacterId(String userId, String characterId);

    void deleteAllByUserId(String userId);
}
