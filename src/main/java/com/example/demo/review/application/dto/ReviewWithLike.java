package com.example.demo.review.application.dto;


import com.example.demo.review.domain.Review;

import java.time.format.DateTimeFormatter;

public record ReviewWithLike(Long reviewId,
                             String placeId,
                             String title,
                             String content,
                             TagValues tagValues,
                             String nickName,
                             String spotId,
                             String createdAt,
                             String modifiedAt,
                             int likeCount,
                             String visitingTime,
                             Double stars) {
    public static ReviewWithLike of(final Review review, final int reviewCount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return new ReviewWithLike(
                review.getId(),
                review.getSpot().getPlaceId(),
                review.getTitle().getValue(),
                review.getContent().getValue(),
                TagValues.of(review.getTag()),
                review.getUsers().getNickName(), // TODO : 여기 처리해야 함 (페치조인하게)
                review.getSpot().getPlaceId(),
                review.getCreatedDate().format(formatter),
                review.getModifiedDate().format(formatter),
                reviewCount,
                review.getVisitingTime().format(formatter),
                review.getStarRank().getValue()
        );
    }

}
