package com.example.community.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRemoveResponseDTO {
    private Long commentId;
    private boolean deleted;
    private LocalDateTime deletedAt;
}
