package com.example.community.post.factory;

import com.example.community.post.dto.PostRequestDTO;
import com.example.community.post.entity.Post;
import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostFactory {
    public Post create(User author, PostRequestDTO requestDTO) {
        return new Post(
                author,
                requestDTO.getTitle(),
                requestDTO.getPostBody(),
                requestDTO.getPostImageUrl()
        );
    }

    public Post create(User author, String title, String postBody, String postImageUrl) {
        return new Post(author, title, postBody, postImageUrl);
    }
}
