package com.example.community.post.controller;

import com.example.community.global.ApiResponse;
import com.example.community.post.dto.*;
import com.example.community.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }
    @PostMapping()
    public ResponseEntity<ApiResponse<PostResponseDTO>> postUpload(@RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody PostRequestDTO requestDTO){
        PostResponseDTO responseDTO = postService.upload(authorizationHeader, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("post_create_success", responseDTO));
    }
    @GetMapping()
    public ResponseEntity<ApiResponse<List<PostListResponseDTO>>> getPostList(@RequestHeader(value = "Authorization") String authorizationHeader){
        List<PostListResponseDTO> listResponse = postService.getPostList(authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("posts_loading_success", listResponse));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponseDTO>> getPostDetail(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable("postId") Long postId){
        PostDetailResponseDTO responseDTO= postService.getPostDetail(authorizationHeader, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("post_loading_success", responseDTO));
    }
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponseDTO>> modifyPost(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable("postId") Long postId, @Valid @RequestBody PostRequestDTO requestDTO){
        PostDetailResponseDTO responseDTO = postService.modifyPost(authorizationHeader, postId, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("post_modify_success", responseDTO));
    }
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@RequestHeader(value = "Authorization") String authorizationHeader, @PathVariable("postId") Long postId){
        postService.deletePost(authorizationHeader, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("post_delete_success", null));
    }
    @PostMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> likePost(@RequestHeader(value = "Authorization") String authroizationHeader, @PathVariable("postId") Long postId){
        LikeResponseDTO responseDTO = postService.likePost(authroizationHeader, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("post_like_success", responseDTO));
    }
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> unlikePost(@RequestHeader(value = "Authorization") String authroizationHeader, @PathVariable("postId") Long postId){
        LikeResponseDTO responseDTO = postService.unlikePost(authroizationHeader, postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("post_unlike_success", responseDTO));
    }
}
