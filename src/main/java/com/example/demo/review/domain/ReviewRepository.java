package com.example.demo.review.domain;

import com.example.demo.review.domain.vo.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryRepository {
    @Query("SELECT COALESCE(AVG(r.starRank), 0) FROM Review r WHERE r.spot.placeId = :placeId")
    Double getAverageStarByPlaceId(@Param(value = "placeId") String placeId);


    @Query("SELECT r FROM Review r JOIN FETCH r.users JOIN FETCH r.spot WHERE r.id = :id")
    Optional<Review> findByIdWithMember(@Param(value = "id") Long id);
}