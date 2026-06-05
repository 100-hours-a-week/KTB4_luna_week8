package com.example.community.post.dto;

import com.example.community.global.dto.AuthorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostResponseDTO {
    private AuthorDTO author;
    private PostDTO post;
}
