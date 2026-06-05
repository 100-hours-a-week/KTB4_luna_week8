package com.example.community.post.repository;

import com.example.community.post.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PostRepository {
    private final Set<Post> posts = new HashSet<>();
    public void save(Post post) {
        posts.add(post);
    }
    public void delete(Post post) {
        posts.remove(post);
    }
    public List<Post> getAllPosts(){
        return posts.stream().sorted(Comparator.comparing(Post::getPostId).reversed()).toList();
    }

    public Optional<Post> getPostByPostId(Long postId){
        return posts.stream()
                .filter(post -> post.getPostId().equals(postId))
                .findFirst();
    }
    public Long nextPostId(){
        return posts.stream().mapToLong(Post::getPostId).max().orElse(0L) +1;
    }
}
