package com.example.demo.review.domain.vo;

import com.example.demo.review.exception.StarException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StarRankTest {
    @Test
    void getInstance() {
        for (StarRank each : StarRank.values()) {
            StarRank instance = StarRank.getInstance(each.getValue());
            assertEquals(each, instance);
        }
    }

    @Test
    void getInstance_null() {
        StarRank instance = StarRank.getInstance(null);
        assertEquals(StarRank.ZERO, instance);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0.1, 1.2, 2.4, 1.56})
    void name(Double value) {
        assertThrows(StarException.InvalidStarRankException.class,
                () -> StarRank.getInstance(value));
    }
}