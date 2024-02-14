package com.example.demo.review.presentation;

import com.example.demo.jwt.CustomUserDetails;
import com.example.demo.review.application.ReviewService;
import com.example.demo.review.application.dto.ReviewRequest;
import com.example.demo.review.application.dto.ReviewUpdateRequest;
import com.example.demo.review.application.dto.ReviewWithLike;
import com.example.demo.review.application.dto.ReviewWriteRequest;
import com.example.demo.review.domain.vo.ReviewId;
import com.example.demo.review_like.application.ReviewLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Review Controller", description = "리뷰 컨트롤러")
@SecurityRequirement(name = "Bearer Authentication")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService likeService;

    // 방문 날짜 (리뷰에)
    @PostMapping("/api/users/spots/{spotId}/reviews")
    @Operation(summary = "리뷰 쓰기", description = "리뷰를 작성합니다.")
    public ResponseEntity<Void> writeReview(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody @Valid ReviewWriteRequest reviewWriteRequest,
            @PathVariable String spotId
    ) {

        LocalDateTime localDate = getLocalDate(reviewWriteRequest.visitingTime());

        final Long id = reviewService.write(
                new ReviewRequest(reviewWriteRequest.title(),
                        reviewWriteRequest.content()),
                customUserDetails.getUserEmail(),
                spotId,
                reviewWriteRequest.tagValues(),
                localDate,
                reviewWriteRequest.starRank());

        final URI location = URI.create("/api/spot/" + spotId + "/reviews" + id);

        return ResponseEntity.created(location).build();
    }

    private static LocalDateTime getLocalDate(String visitingTime) {
        if (visitingTime == null || visitingTime.isEmpty()){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return LocalDateTime.parse(visitingTime, formatter);
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
    @Operation(summary = "리뷰 조회(리스트)", description = "좋아요를 많이 받은 순으로 20개의 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewWithLike>> get20ReviewsByLikes() {
        List<ReviewWithLike> result = likeService.getPopularLists();

        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/users/reviews/{review-id}")
    @Operation(summary = "리뷰 삭제", description = "작성자만 삭제 가능합니다")
    public ResponseEntity<Void> deleteReview(@PathVariable("review-id") Long reviewId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsers().getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/main/test/reviews/{review-id}")
    @Operation(summary = "리뷰 삭제 테스트", description = "아무나 삭제 가능합니다(테스트용)")
    public ResponseEntity<Void> deleteReviewForTest(@PathVariable("review-id") Long reviewId) {
        reviewService.deleteReviewTest(reviewId);

        return ResponseEntity.ok().build();
    }
}