package com.example.demo.review.presentation;

import com.example.demo.global.utils.DateUtils;
import com.example.demo.jwt.CustomUserDetails;
import com.example.demo.review.application.ReviewService;
import com.example.demo.review.application.dto.*;
import com.example.demo.review.domain.vo.Companion;
import com.example.demo.review.domain.vo.PlaceType;
import com.example.demo.review.domain.vo.ReviewId;
import com.example.demo.review.domain.vo.Weather;
import com.example.demo.review_like.application.ReviewLikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class ReviewController implements ReviewControllerSwagger {
    private final ReviewService reviewService;
    private final ReviewLikeService likeService;

    // 방문 날짜 (리뷰에)

    @PostMapping(value = "/api/users/spots/{spotId}/reviews", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> writeReview(
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
    ) {
        LocalDateTime localDate = DateUtils.parseVisitingTime(visitingTime);

        final Long id = reviewService.write(
                new ReviewRequest(title, content),
                customUserDetails.getUsers().getNickName(),
                spotId,
                TagValues.of(new com.example.demo.review.domain.vo.Tag(Weather.getInstance(weather), Companion.getInstance(companion), PlaceType.getInstance(placeType))),
                localDate,
                stars,
                images
        );

        final URI location = URI.create("/api/spot/" + spotId + "/reviews" + id);

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/api/main/spots/reviews/{review-id}")
    @Operation(summary = "리뷰 조회", description = "리뷰 id에 해당하는 리뷰 정보를 불러옵니다.")
    public ResponseEntity<ReviewWithLike> getReview(@PathVariable("review-id") Long reviewId) {
        ReviewWithLike reviewWithLike = likeService.getOneWithLikes(new ReviewId(reviewId));
        return ResponseEntity.ok(reviewWithLike);
    }

    @PutMapping("/api/users/spots/reviews/{review-id}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다. 수정 시 모든 컬럼의 정보가 꼭 필요합니다.")
    public ResponseEntity<Void> updateReview(@RequestBody @Valid ReviewUpdateRequest reviewRequest,
                                             @PathVariable("review-id") Long reviewId) {
        reviewService.updateReview(
                reviewId,
                reviewRequest,
                reviewRequest.tagValues(),
                reviewRequest.visitingTime()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/main/reviews")
    @Operation(summary = "20개 리뷰 조회 (좋아요순, 최신순)", description = "20개의 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewWithLike>> get20ReviewsByLikes(
            @RequestParam SortCondition order
    ) {
        List<ReviewWithLike> result = likeService.getMainReviewList(order);

        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/main/reviews/specifics")
    @Operation(summary = "리뷰 검색해서 조회", description = "검색 조건등에 따라 검색")
    public ResponseEntity<List<ReviewListDTO>> getListWithSearchCondition(
                    @RequestParam String searchValue,
                    @RequestParam(required = false) String weather,
                    @RequestParam(required = false) String companion,
                    @RequestParam(required = false) String placeType,
                    @RequestParam SortCondition order,
                    @RequestParam(required = false) Integer month,
                    @RequestParam(required = false) Integer hour){

        List<ReviewListDTO> result = reviewService.getListWithSearchCondition(
                searchValue,
                TagValues.ofSearchConditions(weather, companion, placeType),
                order,
                month,
                hour
        );

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/users/reviews/{review-id}")
    @Operation(summary = "리뷰 삭제(리스트)", description = "작성자만 삭제 가능합니다")
    public ResponseEntity<Void> deleteReview(@PathVariable("review-id") Long reviewId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsers().getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/main/test/reviews/{review-id}")
    @Operation(summary = "테스트용 삭제", description = "아무나 삭제 가능합니다(테스트용)")
    public ResponseEntity<Void> deleteReviewForTest(@PathVariable("review-id") Long reviewId) {
        reviewService.deleteReviewTest(reviewId);

        return ResponseEntity.ok().build();
    }

}