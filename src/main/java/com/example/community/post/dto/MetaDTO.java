package com.example.community.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetaDTO {
    private int likes;
    private int views;
    private int comments;
    private boolean liked;
}
