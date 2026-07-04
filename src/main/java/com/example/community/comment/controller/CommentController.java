package com.example.community.comment.controller;

import com.example.community.comment.dto.CommentRemoveResponseDTO;
import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.comment.service.CommentService;
import com.example.community.global.ApiResponse;
import com.example.community.user.entity.UserRole;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDTO>> postComment(Authentication authentication, @PathVariable("postId") Long postId, @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        Long userId = getLoginUserId(authentication);
        CommentResponseDTO responseDTO = commentService.uploadComment(postId, userId, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("comment_create_success", responseDTO));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getComments(@PathVariable("postId") Long postId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        List<CommentResponseDTO> responseDTOS = commentService.getComments(postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("comments_get_success", responseDTOS));
    }
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> modifyComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Authentication authentication, @Valid @RequestBody CommentRequestDTO commentRequestDTO){
        Long userId = getLoginUserId(authentication);
        CommentResponseDTO responseDTO = commentService.modifyComment(postId, commentId, userId, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("comment_modify_success", responseDTO));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentRemoveResponseDTO>> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, Authentication authentication) {
        Long userId = getLoginUserId(authentication);
        CommentRemoveResponseDTO commentRemoveResponseDTO= commentService.deleteComment(postId, commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("comment_delete_success", commentRemoveResponseDTO));
    }
    private Long getLoginUserId(org.springframework.security.core.Authentication authentication){
        return Long.valueOf(authentication.getName());
    }

    private UserRole getLoginUserRole(org.springframework.security.core.Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                        authority.equals(UserRole.ROLE_ADMIN.name())
                                || authority.equals(UserRole.ROLE_USER.name())
                )
                .map(UserRole::valueOf)
                .findFirst()
                .orElse(UserRole.ROLE_USER);
    }
}
