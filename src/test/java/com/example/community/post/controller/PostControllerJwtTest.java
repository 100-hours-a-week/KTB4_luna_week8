package com.example.community.post.controller;

import com.example.community.global.auth.JwtTokenProvider;
import com.example.community.global.config.SecurityConfig;
import com.example.community.global.config.filter.JwtFilter;
import com.example.community.global.dto.AuthorDTO;
import com.example.community.post.dto.*;
import com.example.community.post.entity.Post;
import com.example.community.post.service.PostService;
import com.example.community.user.entity.User;
import com.example.community.user.entity.UserRole;
import com.example.community.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class PostControllerJwtTest {

    private static final String TOKEN = "valid-token";

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    PostService postService;
    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken(
                "1",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(jwtTokenProvider.validateToken(TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(TOKEN)).thenReturn(authentication);
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글 목록을 조회할 수 있다.")
    void getPostList_withJwt() throws Exception {
        when(postService.getPostList()).thenReturn(List.of());

        mockMvc.perform(get("/api/posts")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(postService).getPostList();
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글 상세를 조회할 수 있다.")
    void getPostDetail_withJwt() throws Exception {
        when(postService.getPostDetail(UserRole.ROLE_USER, 1L)).thenReturn(postDetailResponse());

        mockMvc.perform(get("/api/posts/1")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(postService).getPostDetail(UserRole.ROLE_USER, 1L);
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글을 작성할 수 있다.")
    void uploadPost_withJwt() throws Exception {
        when(postService.upload(eq(1L), any(PostRequestDTO.class)))
                .thenReturn(postResponse());

        mockMvc.perform(post("/api/posts")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "title",
                              "postBody": "body",
                              "postImageUrl": ""
                            }
                        """))
                .andExpect(status().isCreated());

        verify(postService).upload(eq(1L), any(PostRequestDTO.class));
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글을 수정할 수 있다.")
    void modifyPost_withJwt() throws Exception {
        when(postService.modifyPost(eq(1L), eq(1L), any(PostRequestDTO.class)))
                .thenReturn(postDetailResponse());

        mockMvc.perform(patch("/api/posts/1")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "new title",
                              "postBody": "new body",
                              "postImageUrl": ""
                            }
                        """))
                .andExpect(status().isOk());

        verify(postService).modifyPost(eq(1L), eq(1L), any(PostRequestDTO.class));
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글을 삭제할 수 있다.")
    void deletePost_withJwt() throws Exception {
        mockMvc.perform(delete("/api/posts/1")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(postService).deletePost(1L, 1L);
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글에 좋아요를 누를 수 있다.")
    void likePost_withJwt() throws Exception {
        when(postService.likePost(1L, 1L))
                .thenReturn(new LikeResponseDTO(1L, 1, true));

        mockMvc.perform(post("/api/posts/1/likes")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(postService).likePost(1L, 1L);
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글 좋아요를 취소할 수 있다.")
    void unlikePost_withJwt() throws Exception {
        when(postService.unlikePost(1L, 1L))
                .thenReturn(new LikeResponseDTO(1L, 0, false));

        mockMvc.perform(delete("/api/posts/1/likes")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());

        verify(postService).unlikePost(1L, 1L);
    }

    @Test
    @DisplayName("JWT 인증된 회원은 게시글을 신고할 수 있다.")
    void reportPost_withJwt() throws Exception {
        when(postService.reportPost(eq(1L), eq(1L), any(ReportRequestDTO.class)))
                .thenReturn(new ReportResponseDTO(1L, 1L, true));

        mockMvc.perform(post("/api/posts/1/report")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "reason": "SPAM",
                              "description": "spam"
                            }
                        """))
                .andExpect(status().isCreated());

        verify(postService).reportPost(eq(1L), eq(1L), any(ReportRequestDTO.class));
    }

    @Test
    @DisplayName("JWT가 없으면 게시글 엔드포인트에 접근할 수 없다.")
    void postEndpoint_withoutJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isUnauthorized());
    }


    private PostResponseDTO postResponse() {
        User user = new User(1L, "tester", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        Post post = new Post(user, "title", "body", "");
        ReflectionTestUtils.setField(post, "postId", 1L);

        return new PostResponseDTO(
                new AuthorDTO(UserStatus.ACTIVE, "tester", ""),
                new PostDTO(post)
        );
    }

    private PostDetailResponseDTO postDetailResponse() {
        PostResponseDTO response = postResponse();

        return new PostDetailResponseDTO(
                response.getAuthor(),
                response.getPost(),
                new MetaDTO(0, 0, 0, false)
        );
    }
}