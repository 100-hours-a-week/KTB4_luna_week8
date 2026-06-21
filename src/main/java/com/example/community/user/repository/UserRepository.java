package com.example.community.user.repository;

import com.example.community.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickName);
    boolean existsByNicknameAndUserIdNot(String nickname, Long userId);
}
