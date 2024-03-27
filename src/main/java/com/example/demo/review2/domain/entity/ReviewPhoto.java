//package com.example.demo.review2.domain.entity;
//
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.NoArgsConstructor;
//
//@Entity
//@NoArgsConstructor
//public class ReviewPhoto {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String fileName;
//    private String url;
//
//    @ManyToOne
//    @JoinColumn(name = "review_id")
//    private Review review;
//
//
//    @Builder
//    public ReviewPhoto(String fileName, String url, Review review) {
//        this.fileName = fileName;
//        this.url = url;
//        this.review = review;
//    }
//}
