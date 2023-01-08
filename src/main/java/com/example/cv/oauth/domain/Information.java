package com.example.cv.oauth.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Information {

    public static Map<String, Map<String, String>> google;
    public static Map<String, Map<String, String>> kakao;
    public static Map<String, Map<String, String>> naver;
    public static Map<String, Map<String, String>> github;

    @Value("#{${spring.oauth.google.infos}}")
    public void setGoogle(Map<String, Map<String, String>> google) {
        Information.google = google;
    }

    @Value("#{${spring.oauth.kakao.infos}}")
    public void setKakao(Map<String, Map<String, String>> kakao) {
        Information.kakao = kakao;
    }

    @Value("#{${spring.oauth.naver.infos}}")
    public void setNaver(Map<String, Map<String, String>> naver) {
        Information.naver = naver;
    }

    @Value("#{${spring.oauth.github.infos}}")
    public void setGithub(Map<String, Map<String, String>> github) {
        Information.github = github;
    }
}
