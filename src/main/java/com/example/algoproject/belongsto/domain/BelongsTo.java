package com.example.algoproject.belongsto.domain;

import com.example.algoproject.study.domain.Study;
import com.example.algoproject.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class BelongsTo {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    public BelongsTo(User member, Study study) {
        this.member = member;
        this.study = study;
    }
}
