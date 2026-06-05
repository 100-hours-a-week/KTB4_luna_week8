package com.example.community.comment.entity;

import com.example.community.comment.dto.CommentDTO;
import com.example.community.comment.dto.CommentRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private Long userId;
    private Long postId;

    private String commentBody;
    private LocalDateTime createdAt;

    private boolean modified;
    private LocalDateTime modifiedAt;

    private boolean deleted;
    private LocalDateTime deletedAt;

    public void modify(String commentBody) {
        this.commentBody = commentBody;
        modified = true;
        modifiedAt = LocalDateTime.now();
    }


}
