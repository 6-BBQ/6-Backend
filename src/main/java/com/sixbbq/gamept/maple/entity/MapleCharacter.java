package com.sixbbq.gamept.maple.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maple_characters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapleCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String characterName;

    @Column(nullable = false)
    private String worldName;

    @Column(nullable = false)
    private String characterClass;

    @Column(nullable = false)
    private String ocid;

    @Column(nullable = false)
    private String representativeName;
}