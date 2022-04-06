package com.example.algoproject.solution.domain;

import com.example.algoproject.user.domain.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

//    @ManyToOne
//    @JoinColumn(name = "problem_id")
//    private Problem problemNo;

    private String codeUrl;

    private String readMeUrl;

    private Timestamp date; //등록 날짜/시간

    private String time; //시간복잡도

    private String memory; //공간복잡도

    public Solution(User userId, String codeUrl, String readMeUrl, Timestamp date, String time, String memory) {
        this.userId = userId;
        this.codeUrl = codeUrl;
        this.readMeUrl = readMeUrl;
        this.date = date;
        this.time = time;
        this.memory = memory;
    }
}
