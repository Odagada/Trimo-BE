package com.example.demo.review.application.dto;

import com.example.demo.review.domain.vo.StarRank;
import com.example.demo.review.domain.vo.Tag;
import com.example.demo.review.domain.vo.Title;

import java.time.LocalDateTime;

public record ReviewListData(Long reviewId,
                             Title title,
                             Tag tagValues,
                             String nickName,
                             LocalDateTime visitingTime,
                             StarRank stars,
                             String image) {
}