package com.example.community.comment.repository;

import com.example.community.comment.entity.Comment;
import com.example.community.global.exceptions.ContentNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CommentRepository {
    private final Map<Long, List<Comment>> comments = new HashMap<>();

    public void save(long postId, Comment comment) {
        comments.computeIfAbsent(postId, key -> new ArrayList<>())
                .add(comment);
    }

    public void delete(long postId, Comment comment) {
        comments.computeIfAbsent(postId, key -> new ArrayList<>()).remove(comment);
    }

    public List<Comment> findAllByPostId(long postId){
        return comments.getOrDefault(postId, List.of()).stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList();
    }
    public long nextCommentId(long postId) {
        return comments.getOrDefault(postId, List.of()).stream()
                .mapToLong(Comment::getCommentId)
                .max()
                .orElse(0L) + 1;
    }
    public Optional<Comment> findByPostIdAndCommentId(long postId, long commentId) {
        return comments.get(postId).stream().filter(comment-> comment.getCommentId() == commentId).findFirst();
    }
}
