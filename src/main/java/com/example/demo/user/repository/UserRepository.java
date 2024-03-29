package com.example.demo.user.repository;

import com.example.demo.user.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findByOauthId(String id);

    Optional<Users> findByNickName(String nickName);
}
