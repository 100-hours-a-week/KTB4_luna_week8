package com.example.community.comment.service;

import com.example.community.comment.dto.CommentDTO;
import com.example.community.comment.dto.CommentRemoveResponseDTO;
import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.comment.entity.Comment;
import com.example.community.comment.factory.CommentFactory;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.global.auth.AuthValidator;
import com.example.community.global.dto.AuthorDTO;
import com.example.community.global.exceptions.ContentNotFoundException;
import com.example.community.global.exceptions.NotRegisteredException;
import com.example.community.post.entity.Post;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.entity.User;
import com.example.community.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Validated
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AuthValidator authValidator;
    private final CommentFactory commentFactory;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, AuthValidator authValidator, CommentFactory commentFactory) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.authValidator = authValidator;
        this.commentFactory = commentFactory;
    }
    // ----------------------------------- 댓글 작성 -----------------------------------
    public CommentResponseDTO uploadComment(Long postId, String authenticationHeader, @Valid CommentRequestDTO commentRequestDTO) {
        long authorId = authValidator.getLoginUserId(authenticationHeader);
        User author = userRepository.findUserById(authorId).orElseThrow(NotRegisteredException::new);
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        Comment comment = commentFactory.create(commentRepository.nextCommentId(postId), author.getUserId(), postId, commentRequestDTO);
        commentRepository.save(postId, comment);
        post.increaseComments();
        AuthorDTO authorDTO = new AuthorDTO(author.getStatus(), author.getNickname(), author.getProfileImageUrl());
        CommentDTO commentDTO = toCommentDTO(comment);
        return new CommentResponseDTO(authorDTO, commentDTO);
    }
    // ----------------------------------- 댓글 조회 -----------------------------------
    public List<CommentResponseDTO> getComments(Long postId, String authorizationHeader) {
        authValidator.getLoginUserId(authorizationHeader);
        postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream().map(this::toCommentResponseDTO).toList();
    }
    // ----------------------------------- 댓글 수정 -----------------------------------
    public CommentResponseDTO modifyComment(long postId, long commentId, String authorizationHeader, @Valid CommentRequestDTO commentRequestDTO) {
        long loginUserId = authValidator.getLoginUserId(authorizationHeader);

        postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        Comment comment = commentRepository.findByPostIdAndCommentId(postId, commentId).orElseThrow(ContentNotFoundException::new);

        authValidator.validateOwner(loginUserId, comment.getUserId());

        comment.modify(commentRequestDTO.getCommentBody());

        return toCommentResponseDTO(comment);
    }

    // ----------------------------------- 댓글 삭제 -----------------------------------

    public CommentRemoveResponseDTO deleteComment(long postId, long commentId, String authorizationHeader) {
        long loginUserId = authValidator.getLoginUserId(authorizationHeader);
        Post post = postRepository.getPostByPostId(postId).orElseThrow(ContentNotFoundException::new);
        Comment comment = commentRepository.findByPostIdAndCommentId(postId, commentId).orElseThrow(ContentNotFoundException::new);
        authValidator.validateOwner(loginUserId, comment.getUserId());

        commentRepository.delete(postId, comment);


        return new CommentRemoveResponseDTO(comment.getCommentId(), true, LocalDateTime.now());
    }

    // ----------------------------------- 추가 메서드 -----------------------------------
    private CommentResponseDTO toCommentResponseDTO(Comment comment) {
        User author = userRepository.findUserById(comment.getUserId()).orElseThrow(NotRegisteredException::new);
        AuthorDTO authorDTO;
        if(!author.isActive()) authorDTO = new AuthorDTO(author.getStatus(), "알 수 없음", null);
        else authorDTO = new AuthorDTO(author.getStatus(), author.getNickname(), author.getProfileImageUrl());
        CommentDTO commentDTO = toCommentDTO(comment);
        return new CommentResponseDTO(authorDTO, commentDTO);
    }

    private CommentDTO toCommentDTO(Comment comment) {
        return new CommentDTO(
                comment.getCommentId(),
                comment.getCommentBody(),
                comment.getCreatedAt(),
                comment.isModified(),
                comment.getModifiedAt(),
                comment.isDeleted(),
                comment.getDeletedAt()
        );
    }
}
