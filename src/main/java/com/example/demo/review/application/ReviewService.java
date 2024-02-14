package com.example.demo.review.application;

import com.example.demo.review.application.dto.ReviewRequest;
import com.example.demo.review.application.dto.ReviewUpdateRequest;
import com.example.demo.review.application.dto.TagValues;
import com.example.demo.review.domain.Review;
import com.example.demo.review.domain.ReviewRepository;
import com.example.demo.review.domain.vo.*;
import com.example.demo.review.exception.ReviewException;
import com.example.demo.spot.application.SpotService;
import com.example.demo.spot.domain.Spot;
import com.example.demo.user.domain.entity.Users;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Log4j2
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SpotService spotService;
    private final UserService userService;

    public Review findById(ReviewId reviewId){
        return reviewRepository.findById(reviewId.value())
                .orElseThrow(
                        () -> new ReviewException.ReviewNotFoundException(reviewId.value())
                );
    }
    @Transactional
    public Long write(final ReviewRequest reviewRequest,
                      final String nickName,
                      final String spotId,
                      final TagValues requestTag,
                      final LocalDateTime visitingTime,
                      Double starRank){
        final String title = reviewRequest.title();
        final String content = reviewRequest.content();
        final Spot spot = spotService.findById(spotId);
        final Users user = userService.findByNickName(nickName);

        final Tag tag = getTag(requestTag);

        final Review review = Review.builder()
                .title(new Title(title))
                .content(new Content(content))
                .users(user)
                .spot(spot)
                .tag(tag)
                .visitingTime(visitingTime)
                .starRank(StarRank.getInstance(starRank))
                .build();

        Review savedReview = reviewRepository.save(review);
        return savedReview.getId();
    }

    private static Tag getTag(TagValues requestTag) {
        if (requestTag == null){
             return Tag.ofNone();
        }

        return Tag.of(requestTag);
    }

    @Transactional
    public void updateReview(final Long reviewId,
                             final ReviewUpdateRequest reviewRequest,
                             final TagValues tagValues,
                             final LocalDateTime visitingTime){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(
                        () -> new ReviewException.ReviewNotFoundException(reviewId)
                );

        review.update(getTag(tagValues),
                new Title(reviewRequest.title()),
                new Content(reviewRequest.content()),
                visitingTime
        );

        reviewRepository.save(review);
    }

    public Double getAverageStarRank(String placeID){
        return reviewRepository.getAverageStarByPlaceId(placeID);
    }


    public List<Review> findByLikes(){
        return reviewRepository.findByLikes();
    }

    public void deleteReview(Long id, Long userId){
        Review review = reviewRepository.findById(id)
                .orElseThrow(
                        () -> new ReviewException.ReviewNotFoundException(id)
                );

        if (!review.getId().equals(userId)){
            throw new ReviewException.NotValidUserToDelete(userId);
        }

        reviewRepository.deleteById(id);
    }


    public void deleteReviewTest(Long id){
        reviewRepository.deleteById(id);
    }
}
