package com.example.algoproject.util;

import com.example.algoproject.problem.domain.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PathUtil {

    public String makeGithubPath(Problem problem, String name) {
        //  회사명/문제번호+문제이름/사용자이름
        return problem.getPlatform() + "/[" + problem.getNumber() + "]" + problem.getName() + "/" + name;
    }

    public String makeProblemPath(Problem problem) {
        return problem.getPlatform() + "/[" + problem.getNumber() + "]" + problem.getName();
    }

    public String makeCommitMessage(Problem problem, String name, String request) {
        //  플랫폼 [문제번호]문제이름 By 사용자이름
        return request + problem.getPlatform() + " [" + problem.getNumber() + "]" + problem.getName() + " By " + name;
    }
}
