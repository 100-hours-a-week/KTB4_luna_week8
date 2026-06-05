package com.example.community.user.repository;

import com.example.community.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {
    private final Set<User> users = new HashSet<>();
    public void save(User user) {
        users.add(user);
    }
    public void delete(User user) {
        users.remove(user);
    }
    public Optional<User> findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    public Optional<User> findUserByNickname(String nickname) {
        return users.stream()
                .filter(user -> user.getNickname().equals(nickname))
                .findFirst();
    }
    public Optional<User> findUserById(Long id){
        return users.stream()
                .filter(user -> user.getUserId().equals(id)).
                findFirst();
    }
    public Long nextUserId(){
        return users.stream().mapToLong(User::getUserId).max().orElse(0L) +1;
    }
    public boolean existsByNicknameExceptUserId(String nickname, Long userId){
        return users.stream().anyMatch(user -> user.getNickname().equals(nickname) && !user.getUserId().equals(userId));
    }
}
