package com.example.community.comment.factory;

import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.entity.Comment;
import com.example.community.post.entity.Post;
import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentFactory {
    public Comment create(User author, Post post, Comment parentComment, CommentRequestDTO requestDTO) {
        return new Comment(author, post, parentComment, requestDTO.getCommentBody());
    }
}
