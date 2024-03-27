//package com.example.demo.review2.domain.enums;
//
//import lombok.Getter;
//
//import java.util.Arrays;
//
//@Getter
//public enum Purpose {
//
//    FOODS("맛집"),
//    SIGHT("관광지");
//
//    private final String description;
//
//    Purpose(String description) {
//        this.description = description;
//    }
//
//    public static Purpose getInstance(String description){
//        return Arrays.stream(Purpose.values())
//                .filter(purpose -> purpose.getDescription().equals(description))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("목적이 일치하지 않습니다."));
//    }
//}
