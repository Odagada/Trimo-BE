//package com.example.demo.review2.controller;
//
//import com.example.demo.jwt.CustomUserDetails;
//import com.example.demo.review2.domain.dto.request.CreateReviewRequest;
//import com.example.demo.review2.service.ReviewService;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestPart;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@SecurityRequirement(name = "Bearer Authentication")
//public class Review2Controller {
//
//    private final ReviewService reviewService;
//
//
//    @PostMapping(value = "/api/user/{spot-id}/review/create",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,"multipart/form-data"})
//    public void createReview(@PathVariable("spot-id") Long spotId, @AuthenticationPrincipal CustomUserDetails customUserDetails,@Valid @RequestPart CreateReviewRequest createReviewRequest,
//            @RequestPart List<MultipartFile> reviewImages) {
//        reviewService.create(spotId,customUserDetails, createReviewRequest,reviewImages);
//    }
//}
