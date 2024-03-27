//package com.example.demo.review2.service;
//
//import com.example.demo.jwt.CustomUserDetails;
//import com.example.demo.review.domain.vo.Weather;
//import com.example.demo.review2.domain.dto.request.CreateReviewRequest;
//import com.example.demo.review2.domain.entity.Review;
//import com.example.demo.review2.domain.entity.ReviewPhoto;
//import com.example.demo.review2.domain.enums.Companion;
//import com.example.demo.review2.domain.enums.Purpose;
//import com.example.demo.review2.repository.ReviewJpaRepository;
//import com.example.demo.review2.repository.ReviewPhotoJpaRepository;
//import com.example.demo.s3upload.S3Service;
//import com.example.demo.spot.domain.Spot;
//import com.example.demo.spot.domain.SpotRepository;
//import com.example.demo.user.domain.entity.Users;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ReviewService {
//
//    private final ReviewJpaRepository reviewJpaRepository;
//    private final SpotRepository spotRepository;
//    private final S3Service s3Service;
//    private final ReviewPhotoJpaRepository reviewPhotoJpaRepository;
//
//    @Transactional
//    public Long create(Long spotId, CustomUserDetails customUserDetails, CreateReviewRequest createReviewRequest, List<MultipartFile> reviewImages) {
//        Users users = customUserDetails.getUsers();
//        Spot spot = spotRepository.findById(spotId).orElseThrow(() -> new IllegalArgumentException("등록되지 않은 장소 입니다."));
//
//        Review review = Review.builder()
//                .title(createReviewRequest.getTitle())
//                .content(createReviewRequest.getContent())
//                .spot(spot)
//                .users(users)
//                .visitDateTime(createReviewRequest.getVisitDateTime())
//                .weather(Weather.getInstance(createReviewRequest.getWeather()))
//                .companion(Companion.getInstance(createReviewRequest.getCompanion()))
//                .purpose(Purpose.getInstance(createReviewRequest.getPurpose()))
//                .build();
//
//        reviewJpaRepository.save(review);
//
//        reviewImages.stream().map(
//                file -> {
//                    String url = s3Service.uploadFile(file);
//                    ReviewPhoto reviewPhoto = new ReviewPhoto(file.getOriginalFilename(), url, review);
//                    reviewPhotoJpaRepository.save(reviewPhoto);
//                    return reviewPhoto;
//                });
//
//
//
////        List<ReviewPhoto> reviewimages
////        .stream()
////                .map(file -> {
////                    String url = s3Service.uploadFile(file);
////                    ReviewPhoto reviewPhoto = new ReviewPhoto(file.getOriginalFilename(), url,review);
////                    reviewPhotoJpaRepository.save(reviewPhoto);
////                    return reviewPhoto;
////                })
////                .toList();
////
//
//
//
//        return review.getId();
//
//    }
//}
