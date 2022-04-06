package com.example.algoproject.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PathUtil {

    public String makeGitHubPath(String corpName, String probNo, String probName, String userName) {
        // problem 도메인 생성되면 repoName ~ probName 까지 그거로 대체
        //  회사명/문제번호+문제이름/사용자이름/
        return corpName + "/[" + probNo + "]" + probName + "/" + userName + "/";
    }

    public String makeS3Path(String repoName, String corpName, String probNo, String probName, String userName) {
        //  레포명/회사명/문제번호+문제이름/사용자이름/
        return repoName + "/" + corpName + "/[" + probNo + "]" + probName + "/" + userName + "/";
    }
}
