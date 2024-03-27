package com.example.demo.review.application;

import com.example.demo.common.service.ServiceTest;
import com.example.demo.common.test_instance.ReviewFixture;
import com.example.demo.review.domain.Review;
import com.example.demo.review.domain.vo.ReviewId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewServiceTest extends ServiceTest {
    @Autowired
    ReviewService reviewService;

    @Test
    void findById() {
        final Review expected = entityProvider.saveReview(ReviewFixture.REVIEW_ON_SPOT_1_BY_DK);
        final Review actual = reviewService.findById(new ReviewId(expected.getId()));
        assertEquals(expected, actual);
    }


}