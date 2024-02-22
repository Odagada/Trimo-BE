package com.example.demo.review.domain;

import com.example.demo.common.repository.RepositoryTest;
import com.example.demo.review.application.dto.ReviewListData;
import com.example.demo.review.application.dto.SortCondition;
import com.example.demo.review.domain.vo.Content;
import com.example.demo.review.domain.vo.StarRank;
import com.example.demo.review.domain.vo.Title;
import com.example.demo.review_like.domain.ReviewLike;
import com.example.demo.user.domain.entity.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.demo.common.test_instance.ReviewFixture.REVIEW_ON_SPOT_1_BY_DK;
import static com.example.demo.common.test_instance.ReviewFixture.REVIEW_ON_SPOT_1_BY_DK_ADMIN;
import static com.example.demo.common.test_instance.SpotFixture.SPOT;
import static com.example.demo.common.test_instance.TagFixture.TAG_OF_NONE;
import static com.example.demo.common.test_instance.UserFixture.DK_ADMIN;
import static com.example.demo.common.test_instance.UserFixture.DK_USER;
import static org.assertj.core.api.Assertions.assertThat;

class ReviewQueryRepositoryImplTest extends RepositoryTest {
    @Autowired
    ReviewQueryRepositoryImpl reviewQueryRepository;

    private static final String TEST = "TEST";
    @BeforeEach
    void setUp() {
        entityProvider.saveSpot(SPOT);
        entityProvider.saveUser(DK_USER);
        entityProvider.saveUser(DK_ADMIN);
        saveHundredReviewEntities();
    }

    // 리뷰 엔티티 100개 저장, 81-100 엔티티는 좋아요 저장
    void saveHundredReviewEntities() {
        List<Review> list = IntStream.range(0, 100)
                .mapToObj(
                        value -> Review.builder()
                                .title( new Title(TEST + value))
                                .content(new Content(TEST))
                                .tag(TAG_OF_NONE)
                                .users(DK_USER)
                                .spot(SPOT)
                                .visitingTime(LocalDateTime.now())
                                .starRank(StarRank.ZERO)
                                .build()
                ).toList();

        entityProvider.saveAllReviewAndFlush(list);

        // 80 번 째 이후로 저장
        for (int i = 80; i < list.size(); i++) {
            ReviewLike reviewLike = new ReviewLike(new UserId(1L), (long) i);
            entityProvider.saveReviewLike(reviewLike);
        }
    }

    @Test
    void find20ByLikes_SIZE_IS_20() {
        final List<ReviewListData> byLikes = reviewQueryRepository.findByLikes(SortCondition.POPULAR);
        assertThat(byLikes).hasSize(20);
    }

    @Test
    @DisplayName("좋아요 순으로 정렬된다")
    void find20ByLikes() {
        // given
        Review review = entityProvider.saveReview(REVIEW_ON_SPOT_1_BY_DK);
        Review review2 = entityProvider.saveReview(REVIEW_ON_SPOT_1_BY_DK_ADMIN);

        assertThat(review.getId()).isEqualTo(101L);
        assertThat(review2.getId()).isEqualTo(102L);

        // when
        entityProvider.saveReviewLike(new ReviewLike(new UserId(1L),101L));
        entityProvider.saveReviewLike(new ReviewLike(new UserId(2L),101L));
        // 102 번째 엔티티도 저장 (생성 내림차순이 아님을 테스트 하기 위함)
        entityProvider.saveReviewLike(new ReviewLike(new UserId(1L),102L));

        // then
        List<ReviewListData> byLikes = reviewQueryRepository.findByLikes(SortCondition.POPULAR);
        assertThat(byLikes.get(0).reviewId()).isEqualTo(101);
    }

//    @Test
//    @DisplayName("좋아요 수가 동률이라면 최신순으로 정렬한다.")
//    void find20ByLikes_ORDERS() {
//        List<ReviewListDTO> byLikes = reviewQueryRepository.findByLikes(SortCondition.POPULAR);
//
//        Review firstReview = byLikes.get(0); // 맨 첫번째 유닛, 계속 재할당된다
//        for (int i = 1; i < byLikes.size(); i++) {
//            // 좋아요 수는 현재 동률이므로, 먼저 오는 녀석의 날짜는
//            LocalDateTime first = firstReview.getCreatedDate();
//            // 나중에 오는 녀석의 날짜보다
//            Review lastReview = byLikes.get(i);
//            LocalDateTime last = lastReview.getCreatedDate();
//            // 항상 앞서거나 같다 (나중이다)
//            assertThat((first.isEqual(last)) || first.isAfter(last)).isTrue();
//            // 처음에 올 녀석을 재할당한다
//            firstReview = byLikes.get(i);
//        }
//    }

//    @Test
//    void getListWithSearchCondition_month() {
//        List<ReviewListDTO> reviewList = reviewQueryRepository.getListWithSearchCondition(
//                TEST,
//                Tag.ofNone(),
//                SortCondition.POPULAR,
//                LocalDateTime.now().getMonthValue(),
//                null
//        );
//
//        reviewList.forEach(
//                reviewListDTO -> assertThat(
//                        reviewListDTO.visitingTime().getMonthValue()
//                ).isEqualTo(
//                        LocalDateTime.now().getMonthValue()
//                )
//        );
//    }

//    @Test
//    void getListWithSearchCondition_time() {
//        List<ReviewListDTO> reviewList = reviewQueryRepository.getListWithSearchCondition(
//                TEST,
//                Tag.ofNone(),
//                SortCondition.POPULAR,
//                LocalDateTime.now().getMonthValue(),
//                null
//        );
//
//        reviewList.forEach(
//                reviewListDTO -> assertThat(
//                        reviewListDTO.visitingTime().getHour()
//                ).isEqualTo(
//                        LocalDateTime.now().getHour()
//                )
//        );
//    }
}