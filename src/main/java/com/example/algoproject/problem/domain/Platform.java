package com.example.algoproject.problem.domain;

import java.util.Arrays;

public enum Platform {
    Programmers, Baekjoon, ALGOSPOT;

    public static String[] getList() {
        return Arrays.stream(Platform.values()).map(Enum::name).toArray(String[]::new);
    }
}
