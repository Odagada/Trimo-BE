//package com.example.demo.review2.domain.entity;
//
//import com.example.demo.review.domain.vo.Weather;
//import com.example.demo.review2.domain.enums.Companion;
//import com.example.demo.review2.domain.enums.Purpose;
//import com.example.demo.spot.domain.Spot;
//import com.example.demo.user.domain.entity.Users;
//import jakarta.persistence.*;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@NoArgsConstructor
//public class Review {
//
//    // 필수 필드
//    // 제목 , 내용 , 여행지 , 글쓴이
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String title;
//    private String content;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Spot spot;
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Users users;
//    private LocalDateTime visitDateTime;
//
//    // 선택 필드
//    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
//    private List<ReviewPhoto> images = new ArrayList<>();
//    @Enumerated(EnumType.STRING)
//    private Weather weather;
//    @Enumerated(EnumType.STRING)
//    private Companion companion;
//    @Enumerated(EnumType.STRING)
//    private Purpose purpose;
//
//
//    @Builder
//    public Review(String title, String content, Spot spot, Users users, LocalDateTime visitDateTime, List<ReviewPhoto> images, Weather weather, Companion companion, Purpose purpose) {
//        this.title = title;
//        this.content = content;
//        this.spot = spot;
//        this.users = users;
//        this.visitDateTime = visitDateTime;
//        this.images =  images != null ? new ArrayList<>(images) : new ArrayList<>();
//        this.weather = weather;
//        this.companion = companion;
//        this.purpose = purpose;
//    }
//}
