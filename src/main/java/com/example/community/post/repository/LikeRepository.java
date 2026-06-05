package com.example.community.post.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class LikeRepository {
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    public void save(Long userId, Long postId) {
        likes.computeIfAbsent(postId, key-> new HashSet<>()).add(userId);
    }
    public void delete(Long postId, Long userId) {
        likes.get(postId).remove(userId);
    }
    public boolean exists(Long postId, Long userId) {
        return likes.getOrDefault(postId, Set.of()).contains(userId);
    }
}
