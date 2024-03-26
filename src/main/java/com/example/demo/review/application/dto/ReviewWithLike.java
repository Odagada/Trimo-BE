package com.example.demo.review.application.dto;


import com.example.demo.global.utils.DateUtils;
import com.example.demo.review.domain.Review;
import com.example.demo.review_photo.domain.ReviewPhoto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewWithLike(Long reviewId,
                             String placeId, //
                             String title, //
                             String content, //
                             TagValues tagValues,
                             String nickName,
                             String spotName,
                             String createdAt,
                             String modifiedAt,
                             String visitingTime,
                             Double stars,
                             List<String> images
                             ) {
    public static ReviewWithLike of(final Review review) {
        return new ReviewWithLike(
                review.getId(),
                review.getSpot().getPlaceId(),
                review.getTitle().getValue(),
                review.getContent().getValue(),
                TagValues.of(review.getTag()),
                review.getUsers().getNickName(),
                review.getSpot().getDisplayName(),
                DateUtils.parseTimeToString(review.getCreatedDate()),
                DateUtils.parseTimeToString(review.getModifiedDate()),
                DateUtils.parseTimeToString(review.getVisitingTime()),
                review.getStarRank().getValue(),
                review.getReviewPhotos().stream().map(ReviewPhoto::getUrl).toList()
        );
    }

}
