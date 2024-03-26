package com.example.demo.review.application;

import com.example.demo.global.utils.DateUtils;
import com.example.demo.jwt.CustomUserDetails;
import com.example.demo.review.application.dto.ReviewListDTO;
import com.example.demo.review.application.dto.ReviewListData;
import com.example.demo.review.application.dto.ReviewRequest;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Log4j2
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final SpotService spotService;
    private final UserService userService;
    private final S3Service s3Service;
    private final ReviewPhotoRepository reviewPhotoRepository;


    public Review findById(ReviewId reviewId) {
        return reviewRepository.findByIdWithMember(reviewId.value())
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
    ) {

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
        if (requestTag == null) {
            return Tag.ofNone();
        }

        return Tag.of(requestTag);
    }

    @Transactional
    public void updateReview(
            final CustomUserDetails customUserDetails,
            final String spotId,
            final Long reviewId,
            final String title,
            final String content,
            final TagValues tagValues,
            final LocalDateTime visitingTime,
            final Double stars,
            List<String> images,
            List<MultipartFile> newImages
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(
                        () -> new ReviewException.ReviewNotFoundException(reviewId)
                );

        if (!review.getUsers().equals(customUserDetails.getUsers())) {
            throw new IllegalStateException("자신이 작성한 리뷰만 수정이 가능합니다.");
        }

        Spot spot = spotService.findById(spotId);

        List<ReviewPhoto> reviewPhotos = review.getReviewPhotos().stream().filter(reviewPhoto -> images.contains(reviewPhoto.getUrl())).collect(Collectors.toList());
        List<ReviewPhoto> newReviewPhotos;

        if (newImages != null) {
            newReviewPhotos = Optional.of(newImages)
                    .map(imgList -> {
                        List<String> urls = s3Service.uploadFiles(newImages);
                        return urls.stream()
                                .map(ReviewPhoto::new)
                                .toList();
                    })
                    .orElse(Collections.emptyList());

            reviewPhotoRepository.saveAll(newReviewPhotos);

            reviewPhotos.addAll(newReviewPhotos);
        }


        review.update(getTag(tagValues),
                new Title(title),
                new Content(content),
                visitingTime,
                StarRank.getInstance(stars),
                spot,
                reviewPhotos
        );

        reviewRepository.save(review);
    }

    public long getListWithSearchConditionTotal(String searchValue,
                                                TagValues tagValues,
                                                Integer month,
                                                Integer hour) {
        return reviewRepository.searchConditionTotal(searchValue, Tag.of(tagValues), month, hour);
    }

    public List<ReviewListDTO> getListWithSearchCondition(
            String searchValue,
            TagValues tagValues,
            SortCondition sortCondition,
            Integer month,
            Integer hour,
            int page
    ) {
        List<ReviewListData> dataFromRepository = reviewRepository.getListWithSearchCondition(
                searchValue,
                Tag.of(tagValues),
                sortCondition,
                month,
                hour,
                page
        );

        return dataFromRepository.stream()
                .map(ReviewListDTO::of)
                .toList();
    }

    public Set<String> getMyPlacesIds(Users users) {
        return reviewRepository.getMyPlacedIds(users);
    }

    public List<ReviewListDTO> getMyReviews(Users users,
                                            int page,
                                            TagValues tagValues,
                                            Integer month,
                                            int size) {

        List<ReviewListData> dataFromRepository = reviewRepository.getMyReviews(
                users,
                page,
                Tag.of(tagValues),
                month,
                size
        );

        return dataFromRepository.stream()
                .map(ReviewListDTO::of)
                .toList();
    }

    public Double getAverageStarRank(String placeID) {
        return reviewRepository.getAverageStarByPlaceId(placeID);
    }


    public List<ReviewListData> findByLikes(SortCondition order) {
        return reviewRepository.findByLikes(order);
    }

    public void deleteReview(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(
                        () -> new ReviewException.ReviewNotFoundException(id)
                );

        if (!review.getUsers().getId().equals(userId)) {
            throw new ReviewException.NotValidUserToDelete(userId);
        }

        reviewRepository.deleteById(id);
    }


    public void deleteReviewTest(Long id) {
        reviewRepository.deleteById(id);
    }
}
