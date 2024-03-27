package com.example.demo.review.presentation;

import com.example.demo.global.utils.DateUtils;
import com.example.demo.jwt.CustomUserDetails;
import com.example.demo.review.application.ReviewService;
import com.example.demo.review.application.dto.MyPlacesIds;
import com.example.demo.review.application.dto.ReviewListDTO;
import com.example.demo.review.application.dto.ReviewRequest;
import com.example.demo.review.application.dto.ReviewWithLike;
import com.example.demo.review.application.dto.ReviewWithTotalCount;
import com.example.demo.review.application.dto.SortCondition;
import com.example.demo.review.application.dto.TagValues;
import com.example.demo.review.domain.vo.Companion;
import com.example.demo.review.domain.vo.PlaceType;
import com.example.demo.review.domain.vo.ReviewId;
import com.example.demo.review.domain.vo.Weather;
import com.example.demo.review_like.application.ReviewLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
@Log4j2
@Tag(name = "Review Controller", description = "리뷰 컨트롤러")
@SecurityRequirement(name = "Bearer Authentication")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService likeService;

    // 방문 날짜 (리뷰에)

    @PostMapping(value = "/api/user/spots/{spotId}/reviews", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "리뷰 쓰기", description = "리뷰를 작성합니다.")
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

    @PutMapping(value = "/api/user/spots/reviews/{review-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다. 수정 시 모든 컬럼의 정보가 꼭 필요합니다.")
    public ResponseEntity<Void> updateReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @RequestParam String title,
                                             @RequestParam String content,
                                             @RequestParam String visitingTime,
                                             @RequestParam(required = false) String spotId,
                                             @RequestParam(required = false) String weather,
                                             @RequestParam(required = false) String companion,
                                             @RequestParam(required = false) String placeType,
                                             @RequestParam Double stars,
                                             @PathVariable("review-id") Long reviewId,
                                             @RequestParam List<String> images,
                                             @RequestParam(required = false) List<MultipartFile> newImages
    ) {
        reviewService.updateReview(
                customUserDetails,
                spotId,
                reviewId,
                title,
                content,
                TagValues.of(new com.example.demo.review.domain.vo.Tag(Weather.getInstance(weather), Companion.getInstance(companion), PlaceType.getInstance(placeType))),
                DateUtils.parseVisitingTime(visitingTime),
                stars,
                images,newImages
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/main/reviews")
    @Operation(summary = "20개 리뷰 조회 (좋아요순, 최신순)", description = "20개의 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewListDTO>> get20ReviewsByLikes(
            @RequestParam SortCondition order
    ) {
        List<ReviewListDTO> result = likeService.getMainReviewList(order);

        if (result == null || result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/main/reviews/specifics")
    @Operation(summary = "리뷰 검색해서 조회", description = "검색 조건등에 따라 검색")
    public ResponseEntity<ReviewWithTotalCount> getListWithSearchCondition(
            @RequestParam String searchValue,
            @RequestParam(required = false) String weather,
            @RequestParam(required = false) String companion,
            @RequestParam(required = false) String placeType,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer hour,
            @RequestParam SortCondition order,
            @RequestParam int page) {

        List<ReviewListDTO> result = reviewService.getListWithSearchCondition(
                searchValue,
                TagValues.ofSearchConditions(weather, companion, placeType),
                order,
                month,
                hour,
                page
        );

        if (result.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        long totalCount = reviewService.getListWithSearchConditionTotal(searchValue,
                TagValues.ofSearchConditions(weather, companion, placeType),
                month,
                hour
        );

        return ResponseEntity.ok(new ReviewWithTotalCount(result, totalCount));
    }

    @DeleteMapping("/api/user/reviews/{review-id}")
    @Operation(summary = "리뷰 삭제(리스트)", description = "작성자만 삭제 가능합니다")
    public ResponseEntity<Void> deleteReview(@PathVariable("review-id") Long reviewId,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsers().getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/main/test/reviews/{review-id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "테스트용 삭제", description = "아무나 삭제 가능합니다(테스트용)")
    public ResponseEntity<Void> deleteReviewForTest(@PathVariable("review-id") Long reviewId) {
        reviewService.deleteReviewTest(reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/admin/test/reviews/{review-id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "관리자용 삭제", description = "관리자용 삭제 api")
    public ResponseEntity<Void> deleteReviewByAdmin(@PathVariable("review-id") Long reviewId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("[admin 닉네임 : {}] 관리자용 삭제 api 호출", () -> userDetails.getUsers().getNickName());

        reviewService.deleteReviewTest(reviewId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/user/me/places")
    @Operation(summary = "내가 방문한 장소 목록", description = "내가 방문했던 모든 장소를 리턴합니다")
    public ResponseEntity<MyPlacesIds> getMyPlaceIds (@AuthenticationPrincipal CustomUserDetails userDetails) {
        Set<String> myPlacesIds = reviewService.getMyPlacesIds(userDetails.getUsers());
        return ResponseEntity.ok(new MyPlacesIds(myPlacesIds));
    }

    @GetMapping("/api/user/me/reviews")
    @Operation(summary = "내가 작성한 리뷰 목록", description = "작성일자 기준으로 정렬되며, 6개씩 리턴합니다")
    public ResponseEntity<List<ReviewListDTO>> getMyReviewsPaging(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                  @RequestParam(required = false) String weather,
                                                                  @RequestParam(required = false) String companion,
                                                                  @RequestParam(required = false) String placeType,
                                                                  @RequestParam(required = false) Integer month,
                                                                  @RequestParam int page,
                                                                  @RequestParam @Max(message = "페이지 사이즈는 20 이상일 수 없습니다",value = 20) int size){

        List<ReviewListDTO> myReviews = reviewService.getMyReviews(
                userDetails.getUsers(),
                page,
                TagValues.ofSearchConditions(weather, companion, placeType),
                month,
                size);

        return ResponseEntity.ok(myReviews);
    }
}