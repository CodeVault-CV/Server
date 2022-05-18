package com.example.algoproject.problem.domain;

import java.util.Arrays;
import java.util.List;

public enum Platform {
    Programmers, Baekjoon, ALGOSPOT;

    public static List<String> getList() {
        return Arrays.stream(Platform.values()).map(Enum::name).toList();
    }
}
