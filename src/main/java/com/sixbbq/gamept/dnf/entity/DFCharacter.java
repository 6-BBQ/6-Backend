package com.sixbbq.gamept.dnf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UK_characterId_serverId", columnNames = {"characterId", "serverId"})
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DFCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String characterId;
    private String characterName;
    private String serverId;
    private String adventureName;
    private LocalDateTime lastUpdated;
}
