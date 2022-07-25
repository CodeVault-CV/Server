package com.example.algoproject.study.dto.response;

import com.example.algoproject.study.domain.Study;
import com.example.algoproject.user.dto.UserInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StudyInfo {

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @NotBlank
    private String leader;

    @NotNull
    private List<UserInfo> members;

    public StudyInfo(Study study, List<UserInfo> members) {
        this.id = study.getId();
        this.name = study.getName();
        this.url = study.getRepositoryUrl();
        this.leader = study.getLeaderId();
        this.members = members;
    }
}
