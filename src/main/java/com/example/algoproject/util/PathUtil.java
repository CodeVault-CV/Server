package com.example.algoproject.util;

import com.example.algoproject.problem.domain.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PathUtil {

    public String makeGitHubPath(Problem problem, String name) {
        // problem 도메인 생성되면 repoName ~ probName 까지 그거로 대체
        //  회사명/문제번호+문제이름/사용자이름/
        return problem.getPlatform() + "/[" + problem.getNumber() + "]" + problem.getName() + "/" + name + "/";
    }

    public String makeCommitMessage(Problem problem, String name) {
        //  레포명/회사명/문제번호+문제이름/사용자이름/
        return problem.getPlatform() + " [" + problem.getNumber() + "]" + problem.getName() + " By " + name;
    }
}
