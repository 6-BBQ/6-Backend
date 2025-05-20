package com.sixbbq.gamept.characterRegist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterRegist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;  // 사용자 ID (Member 엔티티 참조)

    @Column(nullable = false)
    private String serverId;  // 서버 ID

    @Column(nullable = false)
    private String characterId;  // 캐릭터 ID

    @Column(nullable = false)
    private String characterName;  // 캐릭터 이름

    @Column(nullable = false)
    private String adventureName;  // 모험단명

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
