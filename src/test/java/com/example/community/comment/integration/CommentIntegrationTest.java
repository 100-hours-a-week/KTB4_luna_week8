package com.example.community.comment.integration;

import com.example.community.comment.entity.Comment;
import com.example.community.comment.repository.CommentRepository;
import com.example.community.post.entity.Post;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserCredential;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import com.example.community.user.repository.UserCredentialRepository;
import com.example.community.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserCredentialRepository userCredentialRepository;

    User postAuthor;
    User commenter;
    User otherUser;
    Post post;

    @BeforeEach
    void setUp(){
        userCredentialRepository.deleteAll();
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        postAuthor = userRepository.save(new User("author", "", UserRole.ROLE_USER, UserStatus.ACTIVE));
        commenter = userRepository.save(new User("commenter", "", UserRole.ROLE_USER, UserStatus.ACTIVE));
        otherUser = userRepository.save(new User("other", "", UserRole.ROLE_USER, UserStatus.ACTIVE));

        userCredentialRepository.save(new UserCredential(commenter, "commenter@test.com", "Test1234!"));
        userCredentialRepository.save(new UserCredential(otherUser, "other@test.com", "Test1234!"));

        post = postRepository.save(new Post(postAuthor, "testpost", "testpostbody", ""));
    }
    @Test
    @DisplayName("댓글 작성 - 저장 확인")
    void uploadComment_success() throws Exception {
        String accessToken = loginAndGetAccessToken();
        mockMvc.perform(post("/api/posts/"+post.getPostId()+"/comments")
                .header("Authorization", "Bearer "+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "commentBody":"test comment"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("comment_create_success"))
                .andExpect(jsonPath("$.data.comment.commentBody").value("test comment"));
        List<Comment> comments = commentRepository.findListByPost(post.getPostId());

        assertThat(comments).hasSize(1);

        Comment savedComment = comments.get(0);

        assertThat(savedComment.getCommentBody()).isEqualTo("test comment");
        assertThat(savedComment.getAuthor().getUserId()).isEqualTo(commenter.getUserId());

        Post savedPost = postRepository.findById(post.getPostId()).orElseThrow();
        assertThat(savedPost.getComments()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 수정 요청 - 변경 확인")
    void modifyComment_success() throws Exception{
        Comment comment = saveCommentByCommenter();
        String accessToken = loginAndGetAccessToken();
        mockMvc.perform(patch("/api/posts/"+post.getPostId()+"/comments/"+comment.getCommentId())
                        .header("Authorization", "Bearer "+accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                           {
                            "commentBody": "modified comment"
                           }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("comment_modify_success"))
                .andExpect(jsonPath("$.data.comment.commentBody").value("modified comment"));
        List<Comment> comments = commentRepository.findListByPost(post.getPostId());

        assertThat(comments).hasSize(1);

        Comment savedComment = commentRepository.findById(comment.getCommentId()).orElseThrow();

        assertThat(savedComment.getCommentBody()).isEqualTo("modified comment");
        assertThat(savedComment.isModified()).isTrue();
        assertThat(savedComment.getModifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("댓글 조회 요청 확인")
    void getComments_success() throws Exception{
        saveCommentByCommenter();
        String accessToken = loginAndGetAccessToken();
        mockMvc.perform(get("/api/posts/"+post.getPostId()+"/comments")
                        .header("Authorization", "Bearer "+accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("comments_get_success"))
                .andExpect(jsonPath("$.data[0].author.nickname").value("commenter"))
                .andExpect(jsonPath("$.data[0].comment.commentBody").value("test comment"));
        List<Comment> comments = commentRepository.findListByPost(post.getPostId());

        assertThat(comments).hasSize(1);

        Comment savedComment = comments.get(0);

        assertThat(savedComment.getCommentBody()).isEqualTo("test comment");
        assertThat(savedComment.getAuthor().getUserId()).isEqualTo(commenter.getUserId());
    }

    @Test
    @DisplayName("댓글 삭제 요청 확인")
    void deleteComments_success() throws Exception{
        Comment comment = saveCommentByCommenter();
        String accessToken = loginAndGetAccessToken();
        mockMvc.perform(delete("/api/posts/"+post.getPostId()+"/comments/"+comment.getCommentId())
                        .header("Authorization", "Bearer "+accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("comment_delete_success"))
                .andExpect(jsonPath("$.data.commentId").value(comment.getCommentId()))
                .andExpect(jsonPath("$.data.deleted").value(true));
        comment = commentRepository.findById(comment.getCommentId()).orElseThrow();
        assertThat(comment.getCommentBody()).isEqualTo("삭제된 댓글입니다");
    }

    private String loginAndGetAccessToken() throws Exception {
        String response = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "commenter@test.com",
                          "password": "Test1234!"
                        }
                    """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = new ObjectMapper().readTree(response);

        return json.get("data").get("token").get("accessToken").asText();
    }
    private Comment saveCommentByCommenter() {
        Comment savedComment = commentRepository.save(
                new Comment(commenter, post, null, "test comment")
        );

        Post savedPost = postRepository.findById(post.getPostId()).orElseThrow();
        savedPost.increaseComments();
        postRepository.save(savedPost);

        return savedComment;
    }
}
