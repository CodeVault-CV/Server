package com.example.algoproject.solution.domain;

import java.util.Arrays;
import java.util.List;

public enum Language {
    cpp, java, javascript, kotlin, python, swift, typescript, none;

    public static List<String> getList() {
        return Arrays.stream(Language.values()).map(Enum::name).toList();
    }
}