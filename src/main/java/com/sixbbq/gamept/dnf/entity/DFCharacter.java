package com.sixbbq.gamept.dnf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "df_character", uniqueConstraints = { // 테이블명 명시 (선택적이지만 권장)
        @UniqueConstraint(name = "UK_characterId_serverId", columnNames = {"characterId", "serverId"})
})
@Getter
@Setter // 필요한 경우에만 Setter 사용 고려 (예: JPA 또는 특정 로직)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DFCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String characterId;

    @Column(nullable = false)
    private String characterName;

    @Column(nullable = false)
    private String serverId;

    private String adventureName; // 상세 조회 시에만 얻을 수 있으므로 nullable

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
