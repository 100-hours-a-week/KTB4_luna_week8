package com.example.community.post.factory;

import com.example.community.post.entity.Post;
import com.example.community.post.entity.PostLike;
import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class    PostLikeFactory {
    public PostLike create(User user, Post post) {
        return new PostLike(user, post);
    }
}
