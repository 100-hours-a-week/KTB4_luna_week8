package com.example.community.comment.controller;

import com.example.community.comment.dto.CommentRemoveResponseDTO;
import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.comment.service.CommentService;
import com.example.community.global.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponseDTO>> postComment(@PathVariable("postId") Long postId, @RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO responseDTO = commentService.uploadComment(postId, authorizationHeader, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("comment_create_success", responseDTO));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponseDTO>>> getComments(@PathVariable("postId") Long postId, @RequestHeader(value = "Authorization") String authorizationHeader) {
        List<CommentResponseDTO> responseDTOS = commentService.getComments(postId, authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("comments_get_success", responseDTOS));
    }
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> modifyComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @RequestHeader(value = "Authorization") String authorizationHeader, @Valid @RequestBody CommentRequestDTO commentRequestDTO){
        CommentResponseDTO responseDTO = commentService.modifyComment(postId, commentId, authorizationHeader, commentRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("comment_modify_success", responseDTO));
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentRemoveResponseDTO>> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @RequestHeader(value= "Authorization") String authorizationHeader) {
        CommentRemoveResponseDTO commentRemoveResponseDTO= commentService.deleteComment(postId, commentId, authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("comment_delete_success", commentRemoveResponseDTO));
    }
}
