package com.mpj.ribbion.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer questionCount = 0;
}
