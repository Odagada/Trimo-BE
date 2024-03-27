package com.example.demo.review.domain;

import com.example.demo.common.repository.RepositoryTest;
import com.example.demo.common.test_instance.TagFixture;
import com.example.demo.review.domain.vo.Content;
import com.example.demo.review.domain.vo.StarRank;
import com.example.demo.review.domain.vo.Title;
import com.example.demo.review_like.domain.ReviewLike;
import com.example.demo.spot.domain.Spot;
import com.example.demo.spot.domain.vo.Location;
import com.example.demo.user.domain.entity.vo.UserId;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.demo.common.test_instance.SpotFixture.SPOT;
import static com.example.demo.common.test_instance.TagFixture.TAG_OF_NONE;
import static com.example.demo.common.test_instance.UserFixture.DK_ADMIN;
import static com.example.demo.common.test_instance.UserFixture.DK_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewRepositoryTest extends RepositoryTest {
    @Autowired
    ReviewRepository reviewRepository;

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
    @Rollback(value = false)
    @Transactional
    void foo() {
        Spot spot = entityProvider.saveSpot(new Spot(
                "foo",
                "disa",
                "for,",
                new Location("asg", "asgaq")
        ));
        Review review = reviewRepository.findById(1L).orElseThrow();
        Title newTitle = new Title("spagnasopg");
        Content newContent = new Content("aspgnasog");
        StarRank newStarRank = StarRank.TWO;

        review.update(
                TagFixture.TAG_OF_NONE,
                newTitle,
                newContent,
                LocalDateTime.now(),
                newStarRank,
                spot,
                null
        );

        assertEquals(newTitle, review.getTitle());
        assertEquals(newContent, review.getContent());
        assertEquals(newStarRank, review.getStarRank());
    }
}