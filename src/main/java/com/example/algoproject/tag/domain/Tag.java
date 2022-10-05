package com.example.algoproject.tag.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Tag {

    @Id
    @Column(name = "tag_id")
    private Long id;

    private String enName;

    private String koName;

    public Tag(Long id, String enName, String koName) {
        this.id = id;
        this.enName = enName;
        this.koName = koName;
    }
}
