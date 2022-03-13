package com.example.algoproject.study.domain;

import com.example.algoproject.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class BelongsTo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 컨트리뷰터 초대의 수락 여부
    boolean accepted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    public BelongsTo(User member, Study study) {
        this.member = member;
        this.study = study;
        this.accepted = false;
    }

    public void acceptInvitation() {
        this.accepted = true;
    }
}
