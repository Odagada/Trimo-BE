package com.example.demo.review.application;

import com.example.demo.review.application.dto.ReviewListDTO;
import com.example.demo.review.application.dto.ReviewRequest;
import com.example.demo.review.application.dto.ReviewUpdateRequest;
import com.example.demo.review.application.dto.SortCondition;
import com.example.demo.review.application.dto.TagValues;
import com.example.demo.review.domain.Review;
import com.example.demo.review.domain.ReviewRepository;
import com.example.demo.review.domain.vo.Content;
import com.example.demo.review.domain.vo.ReviewId;
import com.example.demo.review.domain.vo.StarRank;
import com.example.demo.review.domain.vo.Tag;
import com.example.demo.review.domain.vo.Title;
import com.example.demo.review.exception.ReviewException;
import com.example.demo.review_photo.domain.ReviewPhoto;
import com.example.demo.review_photo.repository.ReviewPhotoRepository;
import com.example.demo.s3upload.S3Service;
import com.example.demo.spot.application.SpotService;
import com.example.demo.spot.domain.Spot;
import com.example.demo.user.domain.entity.Users;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Log4j2
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SpotService spotService;
    private final UserService userService;
    private final S3Service s3Service;
    private final ReviewPhotoRepository reviewPhotoRepository;


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
                      Double starRank,
                      List<MultipartFile> images
    ){

        final String title = reviewRequest.title();
        final String content = reviewRequest.content();
        final Spot spot = spotService.findById(spotId);
        final Users user = userService.findByNickName(nickName);

        List<ReviewPhoto> reviewPhotos = Optional.ofNullable(images)
                .map(imgList -> {
                    List<String> urls = s3Service.uploadFiles(imgList);
                    return urls.stream()
                            .map(ReviewPhoto::new)
                            .toList();
                })
                .orElse(Collections.emptyList());

        reviewPhotoRepository.saveAll(reviewPhotos);

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

        review.getReviewPhotos().addAll(reviewPhotos);

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

    public List<ReviewListDTO> getListWithSearchCondition(
            String searchValue,
            TagValues tagValues,
            SortCondition sortCondition,
            Integer month,
            Integer hour
    ){
        return reviewRepository.getListWithSearchCondition(
                searchValue,
                Tag.of(tagValues),
                sortCondition,
                month,
                hour
        );
    }

    public Double getAverageStarRank(String placeID){
        return reviewRepository.getAverageStarByPlaceId(placeID);
    }


    public List<Review> findByLikes(SortCondition order){
        return reviewRepository.findByLikes(order);
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
