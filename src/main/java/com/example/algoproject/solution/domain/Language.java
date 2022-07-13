package com.example.algoproject.solution.domain;

import java.util.Arrays;
import java.util.List;

public enum Language {
    c, cpp, java, js, py;

    public static List<String> getList() {
        return Arrays.stream(Language.values()).map(Enum::name).toList();
    }
}