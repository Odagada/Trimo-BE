//package com.example.demo.review2.domain.enums;
//
//import lombok.Getter;
//
//import java.util.Arrays;
//
//@Getter
//public enum Companion {
//    SOLE("혼자"),
//    COUPLE("커플"),
//    FAMILY("가족"),
//    FRIENDS("친구");
//
//    private final String description;
//
//    Companion(String description) {
//        this.description = description;
//    }
//
//    public static Companion getInstance(String description) {
//        return Arrays.stream(Companion.values())
//                .filter(companion -> companion.getDescription().equals(description))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("일치하는 동행인이 없습니다."));
//    }
//}
