package com.example.community.post.service;

import com.example.community.global.auth.AuthValidator;
import com.example.community.global.dto.AuthorDTO;
import com.example.community.global.exceptions.ConflictException;
import com.example.community.global.exceptions.ContentNotFoundException;
import com.example.community.global.exceptions.NotRegisteredException;
import com.example.community.post.dto.*;
import com.example.community.post.entity.Post;
import com.example.community.post.entity.PostStatus;
import com.example.community.post.factory.PostFactory;
import com.example.community.post.repository.LikeRepository;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.entity.User;
import com.example.community.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class PostService {
    private final PostRepository postRepository;
    private final AuthValidator authValidator;
    private final UserRepository userRepository;
    private final PostFactory postFactory;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, AuthValidator authValidator, UserRepository userRepository, PostFactory postFactory, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.authValidator = authValidator;
        this.userRepository = userRepository;
        this.postFactory = postFactory;
        this.likeRepository = likeRepository;
    }
    // ----------------------------------- 게시물 업로드 -----------------------------------
    public PostResponseDTO upload(String authorizationHeader, @Valid PostRequestDTO postRequestDTO) {
        long authorId = authValidator.getLoginUserId(authorizationHeader);
        User author = userRepository.findUserById(authorId).orElseThrow(NotRegisteredException::new);

        Post post = postFactory.create(postRepository.nextPostId(), authorId, postRequestDTO);
        postRepository.save(post);
        AuthorDTO authorDTO = new AuthorDTO(author.getStatus(), author.getNickname(), author.getProfileImageUrl());
        PostDTO postDTO = new PostDTO(post);

        return new PostResponseDTO(authorDTO, postDTO);
    }

    // ----------------------------------- 게시물 목록 조회 -----------------------------------
    public List<PostListResponseDTO> getPostList(String authorizationHeader){
        authValidator.getLoginUserId(authorizationHeader);
        List<Post> postList = postRepository.getAllPosts();
        return postList.stream().map(this::toPostListResponseDTO).toList();
    }

    // ----------------------------------- 게시물 상세 조회 -----------------------------------
    public PostDetailResponseDTO getPostDetail(String authorizationHeader, Long postId){
        authValidator.getLoginUserId(authorizationHeader);
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        User author = userRepository.findUserById(post.getUserId()).orElseThrow(NotRegisteredException::new);
        AuthorDTO authorDTO = new AuthorDTO(author.getStatus(), author.getNickname(), author.getProfileImageUrl());
        post.increaseViews();
        return new PostDetailResponseDTO(authorDTO, new PostDTO(post), toMetaDTO(post));
    }

    // ----------------------------------- 게시물 수정 -----------------------------------
    public PostDetailResponseDTO modifyPost(String authorizationHeader, Long postId, @Valid PostRequestDTO postRequestDTO){
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        authValidator.validateOwner(authorizationHeader, post.getUserId());
        User author = userRepository.findUserById(post.getUserId()).orElseThrow(NotRegisteredException::new);
        AuthorDTO authorDTO = new AuthorDTO(author.getStatus(), author.getNickname(), author.getProfileImageUrl());

        post.modifyPost(postRequestDTO.getTitle(), postRequestDTO.getPostBody(), postRequestDTO.getPostImageUrl());
        return new PostDetailResponseDTO(authorDTO, new PostDTO(post), toMetaDTO(post));
    }

    // ----------------------------------- 게시물 삭제 -----------------------------------
    public void deletePost(String authorizationHeader, Long postId){
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        authValidator.validateOwner(authorizationHeader, post.getUserId());
        post.deletePost();
    }

    // ----------------------------------- 좋아요 추가 -----------------------------------
    public LikeResponseDTO likePost(String authorizationHeader, Long postId){
        long userId =  authValidator.getLoginUserId(authorizationHeader);
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        if(likeRepository.exists(postId, userId)) throw new ConflictException();
        likeRepository.save(postId, userId);
        post.increaseLikes();
        return new LikeResponseDTO(postId, post.getLikes(), true);
    }
    // ----------------------------------- 좋아요 삭제 -----------------------------------
    public LikeResponseDTO unlikePost(String authorizationHeader, Long postId){
        long userId =  authValidator.getLoginUserId(authorizationHeader);
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        if(!likeRepository.exists(postId, userId)) throw new ConflictException();
        likeRepository.delete(postId, userId);
        post.decreaseLikes();
        return new LikeResponseDTO(postId, post.getLikes(), false);
    }
    // ----------------------------------- 추가 메서드 -----------------------------------

    private PostListResponseDTO toPostListResponseDTO(Post post){
        if(PostStatus.BLINDED.equals(post.getStatus())){
            PostItemDTO postItemDTO = new PostItemDTO(post.getPostId(), "숨김 처리된 게시글", post.getCreatedAt(), post.getLikes(), post.getComments(), post.getViews());
            return new PostListResponseDTO(null, postItemDTO);
        }
        User author = userRepository.findUserById(post.getUserId()).orElseThrow(NotRegisteredException::new);
        AuthorDTO authorDTO = new AuthorDTO(author.getStatus(), author.getNickname(), author.getProfileImageUrl());

        PostItemDTO postItemDTO =  new PostItemDTO(post);
        return new PostListResponseDTO(authorDTO, postItemDTO);
    }

    private MetaDTO toMetaDTO(Post post){
        return new MetaDTO(post.getLikes(), post.getViews(), post.getComments(), false);
    }

}
