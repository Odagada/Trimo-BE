package com.example.demo.review.domain.vo;

import com.example.demo.global.converter.AbstractCodedEnumConverter;
import com.example.demo.global.converter.CodedEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Weather implements CodedEnum<String> {
    SUNNY("맑음"),
    RAINY("우천"),
    CLOUDY("흐림"),
    SNOWY("눈"),
    NONE("모르겠음")
    ;

    private final String description;

    Weather(String description) {
        this.description = description;
    }

    public static Weather getInstance(String description){
        return Arrays.stream(values())
                .filter(weather -> weather.description.equals(description))
                .findAny()
                .orElse(NONE);
    }

    @Override
    public String getCode() {
        return description;
    }
    @jakarta.persistence.Converter(autoApply = true)
    static class Converter extends AbstractCodedEnumConverter<Weather, String>{
        public Converter() {
            super(Weather.class);
        }
    }
}