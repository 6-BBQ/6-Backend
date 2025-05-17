package com.sixbbq.gamept.maple.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "maple_characters")
@Getter
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
    private String accountId;
    
    @Column(nullable = false)
    private String characterClass;
    
    @Column(nullable = false)
    private String ocid;
}
