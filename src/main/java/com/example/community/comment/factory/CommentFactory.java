package com.example.community.comment.factory;

import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.entity.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentFactory {
    public Comment create(long commentId, long authorId, long postId, CommentRequestDTO commentRequestDTO) {
        return new Comment(commentId, authorId, postId, commentRequestDTO.getCommentBody(), LocalDateTime.now(), false, null, false, null);
    }
}
