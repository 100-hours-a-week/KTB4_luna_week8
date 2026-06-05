package com.example.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponseDTO {
    private long postId;
    private int likes;
    private boolean liked;
}
