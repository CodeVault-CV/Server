package com.example.algoproject.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    Role role() default Role.USER;

    enum Role {
        USER, // 일반 사용자
        LEADER, // 스터디의 팀장
        MEMBER, // 스터디에 속한 멤버
    }
}
