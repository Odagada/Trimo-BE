package com.example.demo.user.domain.entity;

import com.example.demo.global.domain.BaseTimeEntity;
import com.example.demo.user.domain.enums.Gender;
import com.example.demo.user.domain.request.RequiredUserInfoRequest;
import com.example.demo.user.domain.request.UpdateUserRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString
public class Users extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickName;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int age;

    private String oauthId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Users(String email, String nickName, String imageUrl, Gender gender, int age, String oauthId, Role role) {
        this.email = email;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.gender = gender;
        this.age = age;
        this.oauthId = oauthId;
        this.role = role;
    }

    public void updateUserInfo(UpdateUserRequest updateUserRequest) {
        this.email = updateUserRequest.getEmail();
        this.nickName = updateUserRequest.getNickName();
        this.imageUrl = updateUserRequest.getImageUrl();
        this.gender = Gender.getInstance(updateUserRequest.getGender());
        this.age = updateUserRequest.getAge();
    }

    public void updateEssentials(RequiredUserInfoRequest requiredUserInfoRequest) {
        this.nickName = requiredUserInfoRequest.getNickName();
        this.imageUrl = requiredUserInfoRequest.getImageUrl();
        this.gender = Gender.getInstance(requiredUserInfoRequest.getGender());
        this.age = requiredUserInfoRequest.getAge();
        this.role = Role.USER;

    }

}
