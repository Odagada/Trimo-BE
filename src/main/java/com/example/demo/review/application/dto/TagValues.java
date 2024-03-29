package com.example.demo.review.application.dto;

import com.example.demo.review.domain.vo.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.netty.util.internal.StringUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TagValues(
        String weather,
        String companion,
        String placeType
) {
    public static TagValues of(Tag tag) {
        return new TagValues(
                tag.getWeather().getDescription().equals("모르겠음") ? null : tag.getWeather().getDescription(),
                tag.getCompanion().getDescription().equals("모르겠음") ? null : tag.getCompanion().getDescription(),
                tag.getPlaceType().getDescription().equals("모르겠음") ? null : tag.getPlaceType().getDescription()
        );
    }

    public static TagValues ofSearchConditions(String weather,
                                               String companion,
                                               String placeType){
        return new TagValues(
                StringUtil.isNullOrEmpty(weather) ? null : weather,
                StringUtil.isNullOrEmpty(companion) ? null : companion,
                StringUtil.isNullOrEmpty(placeType) ? null : placeType
        );
    }
}