package com.example.demo.review.domain;

import com.example.demo.common.test_instance.SpotFixture;
import com.example.demo.common.test_instance.UserFixture;
import com.example.demo.review.domain.vo.Content;
import com.example.demo.review.domain.vo.Tag;
import com.example.demo.review.domain.vo.Title;
import com.example.demo.spot.domain.Spot;
import com.example.demo.user.domain.entity.Users;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.example.demo.common.test_instance.ReviewFixture.REVIEW_OF_ALL_FIELD_VALUE_IS_FOO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReviewTest {
    private static final String FOO = "FOO";
    private static final Review REVIEW = REVIEW_OF_ALL_FIELD_VALUE_IS_FOO;

    @Test
    void equalsTest() {
        Title title = REVIEW.getTitle();
        Content content = REVIEW.getContent();
        Tag tag = REVIEW.getTag();
        Spot spot = REVIEW.getSpot();
        Users users = REVIEW.getUsers();

        assertEquals(content, new Content(FOO));
        assertEquals(SpotFixture.FOO_SPOT, spot);
        assertEquals(UserFixture.FOO, users);
        assertEquals(title, new Title(FOO));
        assertEquals(Tag.ofNone(), tag);
    }

    @Test
    void updateTest() {
        // 업데이트 후 값 확인
        Title newTitle = new Title("New Title");
        Content newContent = new Content("New Content");

        Review review = new Review(
                REVIEW.getTitle(),
                REVIEW.getContent(),
                REVIEW.getTag(),
                REVIEW.getUsers(),
                REVIEW.getSpot(),
                REVIEW.getVisitingTime(),
                REVIEW.getStarRank()
        );
        review.update(Tag.ofNone(), newTitle, newContent, LocalDateTime.now());

        assertEquals(review.getTitle(), newTitle);
        assertEquals(review.getContent(), newContent);
        assertEquals(review.getTag(), Tag.ofNone());
    }

    @Test
    void hashCodeTest() {
        assertEquals(REVIEW.hashCode(), REVIEW.hashCode());
    }
}