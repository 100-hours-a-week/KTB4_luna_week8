package com.example.community.comment.dto;

import com.example.community.global.dto.AuthorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponseDTO {
    private AuthorDTO author;
    private CommentDTO comment;
}
