package com.example.demo.review_like.application;

import com.example.demo.common.service.ServiceTest;
import com.example.demo.common.test_instance.ReviewFixture;
import com.example.demo.review.application.dto.ReviewWithLike;
import com.example.demo.review.domain.vo.ReviewId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.demo.common.test_instance.SpotFixture.SPOT;
import static com.example.demo.common.test_instance.UserFixture.DK_ADMIN;
import static com.example.demo.common.test_instance.UserFixture.DK_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReviewLikeServiceTest extends ServiceTest {
    @Autowired
    ReviewLikeService reviewLikeService;

    @BeforeEach
    void setUp() {
        entityProvider.saveSpot(SPOT);
        entityProvider.saveUser(DK_USER);
        entityProvider.saveReview(ReviewFixture.REVIEW_ON_SPOT_1_BY_DK);
    }
    @Test
    void name() {
        ReviewWithLike oneWithLikes = reviewLikeService.getOneWithLikes(new ReviewId(1L));
        assertThat(oneWithLikes.spotName()).isEqualTo(SPOT.getDisplayName());
    }
}