package com.example.community.comment.service;

import com.example.community.comment.dto.CommentRemoveResponseDTO;
import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.comment.entity.Comment;
import com.example.community.comment.factory.CommentFactory;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.global.auth.AuthValidator;
import com.example.community.global.auth.JwtToken;
import com.example.community.global.exceptions.ForbiddenException;
import com.example.community.global.mapper.AuthorMapper;
import com.example.community.post.entity.Post;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import com.example.community.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentFactory commentFactory;
    @Mock
    AuthValidator authValidator;
    @Mock
    AuthorMapper authorMapper;

    @InjectMocks
    CommentService commentService;

    User author;
    User commenter;
    Post post;
    Comment comment;
    JwtToken jwtToken;

    CommentRequestDTO commentRequestDTO;
    CommentRequestDTO modifyRequestDTO;

    @BeforeEach
    void setUp(){
        author = new User(1L, "author", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        post= new Post(author, "test", "testBody", "testImage");
        ReflectionTestUtils.setField(post, "postId", 1L);
        commenter = new User(2L, "commenter", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        comment = new Comment(commenter, post, null, "test comment");
        ReflectionTestUtils.setField(comment, "commentId", 1L);
        jwtToken = new JwtToken("Bearer", "access-token", "refresh-token");

        commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setCommentBody("test comment");

        modifyRequestDTO = new CommentRequestDTO();
        modifyRequestDTO.setCommentBody("modified comment");
    }
    @Test
    @DisplayName("댓글 작성 성공시 post의 댓글 수도 1 오른다.")
    void upload_success(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(commenter));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentFactory.create(commenter, post, null, commentRequestDTO)).thenReturn(comment);

        CommentResponseDTO response = commentService.uploadComment(post.getPostId(),commenter.getUserId(), commentRequestDTO);
        assertThat(response.getComment().getCommentBody()).isEqualTo("test comment");
        assertThat(post.getComments()).isEqualTo(1);
    }
    @Test
    @DisplayName("댓글 작성 실패 시 post의 댓글 수가 오르지 않는다.")
    void upload_fail(){
        DataAccessResourceFailureException saveException = new DataAccessResourceFailureException("comment save failed");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(commenter));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentFactory.create(commenter, post, null, commentRequestDTO)).thenReturn(comment);
        when(commentRepository.save(any())).thenThrow(saveException);

        assertThatThrownBy(()->commentService.uploadComment(post.getPostId(), commenter.getUserId(), commentRequestDTO)).isSameAs(saveException);
        assertThat(post.getComments()).isEqualTo(0);
        verify(commentRepository).save(comment);
    }
    @Test
    @DisplayName("댓글 조회 성공")
    void getCommentList_success(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findListByPost(anyLong())).thenReturn(List.of());

        List<CommentResponseDTO> response = commentService.getComments(1L);
        assertThat(response).isEqualTo(List.of());
    }
    @Test
    @DisplayName("댓글 수정 성공")
    void modifyComment_success(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findById(2L)).thenReturn(Optional.of(commenter));
        when(commentRepository.findCommentWithPost(anyLong(), anyLong())).thenReturn(Optional.of(comment));

        CommentResponseDTO response = commentService.modifyComment(1L, 1L, 2L, modifyRequestDTO);
        assertThat(response.getComment().getCommentBody()).isEqualTo("modified comment");
        assertThat(comment.isModified()).isTrue();
    }
    @Test
    @DisplayName("댓글 작성자가 아니면 수정 요청 시 403")
    void modifyComment_notOwner_throwsForbiddenException(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.findCommentWithPost(anyLong(), anyLong())).thenReturn(Optional.of(comment));
        doThrow(new ForbiddenException()).when(authValidator).validateOwner(author.getUserId(), commenter.getUserId());

        assertThatThrownBy(()->commentService.modifyComment(1L, 1L, 1L, modifyRequestDTO)).isInstanceOf(ForbiddenException.class);
        assertThat(comment.isModified()).isFalse();
    }
    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_success(){
        when(commentRepository.findCommentWithPost(anyLong(), anyLong())).thenReturn(Optional.of(comment));

        CommentRemoveResponseDTO response = commentService.deleteComment(1L, 1L, 2L);
        assertThat(response.isDeleted()).isTrue();
        assertThat(comment.isDeleted()).isTrue();
    }
    @Test
    @DisplayName("댓글 작성자가 아니면 삭제 요청 시 403")
    void deleteComment_notOwner_throwsForbiddenException(){
        when(commentRepository.findCommentWithPost(anyLong(), anyLong())).thenReturn(Optional.of(comment));
        doThrow(new ForbiddenException()).when(authValidator).validateOwner(author.getUserId(), commenter.getUserId());

        assertThatThrownBy(()->commentService.deleteComment(1L, 1L, 1L)).isInstanceOf(ForbiddenException.class);
        assertThat(comment.isDeleted()).isFalse();
    }
}
