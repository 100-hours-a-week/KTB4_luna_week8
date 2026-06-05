package com.example.community.post.factory;

import com.example.community.post.dto.PostRequestDTO;
import com.example.community.post.entity.Post;
import com.example.community.post.entity.PostStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostFactory {
    public Post create(Long postId, Long authorId, PostRequestDTO requestDTO) {
        return new Post(
                postId,
                authorId,
                requestDTO.getTitle(),
                requestDTO.getPostBody(),
                requestDTO.getPostImageUrl(),
                LocalDateTime.now(),
                false,
                null,
                1,
                PostStatus.ACTIVE,
                0,
                0,
                0
        );
    }
}
