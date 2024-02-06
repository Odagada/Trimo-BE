package com.example.demo.oauth;

import java.util.Arrays;
import java.util.Map;

public enum OAuthAttributes {
    GOOGLE("google") {
        @Override
        public UserProfile of(Map<String, Object> attributes) {
            return UserProfile.builder()
                    .oauthId(String.valueOf(attributes.get("sub")))
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .imageUrl((String) attributes.get("picture"))
                    .build();
        }
    },
    NAVER("naver") {
        @Override
        public UserProfile of(Map<String, Object> attributes) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return UserProfile.builder()
                    .oauthId((String) response.get("id"))
                    .email((String) response.get("email"))
                    .name((String) response.get("name"))
                    .imageUrl((String) response.get("profile_image"))
                    .build();
        }
    },
    KAKAO("kakao") {
        @Override
        public UserProfile of(Map<String, Object> attributes) {

            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

            return UserProfile.builder()
                    .oauthId(String.valueOf(attributes.get("id")))
                    .email((String) kakaoAccount.get("email"))
                    .name((String) properties.get("nickname"))
                    .imageUrl((String) properties.get("profile_image"))
                    .build();
        }
    };

    private final String providerName;

    OAuthAttributes(String providerName) {
        this.providerName = providerName;
    }

    public static UserProfile extract(String providerName, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> providerName.equals(provider.providerName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .of(attributes);
    }

    public abstract UserProfile of(Map<String, Object> attributes);

}