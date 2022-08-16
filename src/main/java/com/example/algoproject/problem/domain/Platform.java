package com.example.algoproject.problem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Platform {
    programmers("https://programmers.co.kr/learn/courses/30/lessons/"),
    boj("https://www.acmicpc.net/problem/");

    private final String url;
}
