package com.example.demo.test;

import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.user.domain.entity.Users;
import com.example.demo.user.domain.enums.Gender;
import com.example.demo.user.domain.enums.Provider;
import com.example.demo.user.domain.enums.Role;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Log4j2
public class TestService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(String email){

        userRepository.save(new Users(
                email,
                "",
                Gender.MALE,
                LocalDate.now(),
                email,
                Provider.GOOGLE,
                Role.ADMIN
                ));

        return jwtTokenProvider.createToken(email);
    }
}
