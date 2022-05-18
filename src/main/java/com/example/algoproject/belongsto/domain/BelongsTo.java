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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 컨트리뷰터 초대의 수락 여부
    private boolean accepted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    public BelongsTo(User leader, Study study, boolean accepted) {
        this.member = leader;
        this.study = study;
        this.accepted = accepted;
    }

    public void acceptInvitation() {
        this.accepted = true;
    }
}
