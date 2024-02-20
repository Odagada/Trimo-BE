package com.example.demo.review.presentation;

import com.example.demo.jwt.CustomUserDetails;
import com.example.demo.review.application.dto.*;
import com.example.demo.review.domain.vo.ReviewId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review Controller", description = "리뷰 컨트롤러")
@SecurityRequirement(name = "Bearer Authentication")
interface ReviewControllerSwagger {
    @Operation(summary = "리뷰 쓰기", description = "리뷰를 작성합니다.")
    ResponseEntity<Void> writeReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String spotId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String visitingTime,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) String companion,
            @RequestParam(required = false) String placeType,
            @RequestParam Double stars,
            @RequestParam(required = false) List<MultipartFile> images
    );

    @GetMapping("/api/main/spots/reviews/{review-id}")
    @Operation(summary = "리뷰 조회", description = "리뷰 id에 해당하는 리뷰 정보를 불러옵니다.")
    ResponseEntity<ReviewWithLike> getReview(@PathVariable("review-id") Long reviewId);

    @PutMapping("/api/users/spots/reviews/{review-id}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다. 수정 시 모든 컬럼의 정보가 꼭 필요합니다.")
    ResponseEntity<Void> updateReview(@RequestBody @Valid ReviewUpdateRequest reviewRequest,
                                      @PathVariable("review-id") Long reviewId);


    @Operation(summary = "20개 리뷰 조회 (좋아요순, 최신순)", description = "20개의 리뷰를 조회합니다.")
    ResponseEntity<List<ReviewWithLike>> get20ReviewsByLikes(
            @RequestParam SortCondition order
    );


    @Operation(summary = "리뷰 검색해서 조회", description = "검색 조건등에 따라 검색")
    ResponseEntity<List<ReviewListDTO>> getListWithSearchCondition(
            @RequestParam String searchValue,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) String companion,
            @RequestParam(required = false) String placeType,
            @RequestParam SortCondition order,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer hour);


    @Operation(summary = "리뷰 삭제 (작성자만)", description = "작성자만 삭제 가능합니다")
    ResponseEntity<Void> deleteReview(@PathVariable("review-id") Long reviewId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(summary = "테스트용 삭제", description = "아무나 삭제 가능합니다(테스트용)")
    ResponseEntity<Void> deleteReviewForTest(@PathVariable("review-id") Long reviewId);

}