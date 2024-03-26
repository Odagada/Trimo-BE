package com.example.demo.review.application.dto;

import com.example.demo.global.utils.DateUtils;

public record ReviewListDTO(Long reviewId,
                            String title,
                            TagValues tagValues,
                            String nickName,
                            String visitingTime,
                            Double stars,
                            String image) {

    public static ReviewListDTO of (ReviewListData reviewListData){
        return new ReviewListDTO(
                reviewListData.reviewId(),
                reviewListData.title().getValue(),
                TagValues.of(reviewListData.tagValues()),
                reviewListData.nickName(),
                DateUtils.parseTimeToString(reviewListData.visitingTime()),
                reviewListData.stars().getValue(),
                reviewListData.image());
    }
}
