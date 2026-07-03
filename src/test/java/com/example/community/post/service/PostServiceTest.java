package com.example.community.post.service;

import com.example.community.global.auth.AuthValidator;
import com.example.community.global.auth.TokenRepository;
import com.example.community.global.exceptions.ContentNotFoundException;
import com.example.community.global.exceptions.ForbiddenException;
import com.example.community.global.exceptions.NotRegisteredException;
import com.example.community.global.mapper.AuthorMapper;
import com.example.community.post.dto.PostDetailResponseDTO;
import com.example.community.post.dto.PostListResponseDTO;
import com.example.community.post.dto.PostRequestDTO;
import com.example.community.post.dto.PostResponseDTO;
import com.example.community.post.entity.Post;
import com.example.community.post.entity.PostRevision;
import com.example.community.post.entity.PostStatus;
import com.example.community.post.factory.PostFactory;
import com.example.community.post.factory.PostLikeFactory;
import com.example.community.post.factory.ReportFactory;
import com.example.community.post.repository.PostLikeRepository;
import com.example.community.post.repository.PostRepository;
import com.example.community.post.repository.PostRevisionRepository;
import com.example.community.post.repository.ReportRepository;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PostLikeRepository postLikeRepository;
    @Mock
    ReportRepository reportRepository;
    @Mock
    PostRevisionRepository postRevisionRepository;
    @Spy
    PostFactory postFactory = new PostFactory();
    @Spy
    PostLikeFactory postLikeFactory = new PostLikeFactory();
    @Spy
    ReportFactory reportFactory = new ReportFactory();
    @Spy
    AuthorMapper authorMapper = new AuthorMapper();
    @Spy
    AuthValidator authValidator = new AuthValidator();
    @InjectMocks
    PostService postService;

    User user;
    User otherUser;
    Post post;

    @BeforeEach
    void setUp() {
        user = new User(1L, "tester", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        otherUser = new User(2L, "other", "", UserRole.ROLE_USER, UserStatus.ACTIVE);
        post = new Post(user, "title", "body", "");
        ReflectionTestUtils.setField(post, "postId", 1L);
    }

    @Test
    @DisplayName("로그인 유저 id로 게시글을 작성한다.")
    void upload_createsPostWithLoginUser() {
        PostRequestDTO request = postRequest("title", "body", "");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "postId", 1L);
            return saved;
        });

        PostResponseDTO response = postService.upload(1L, request);

        assertThat(response.getAuthor().getNickname()).isEqualTo("tester");
        assertThat(response.getPost().getPostId()).isEqualTo(1L);
        assertThat(response.getPost().getTitle()).isEqualTo("title");
        assertThat(response.getPost().getPostBody()).isEqualTo("body");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 작성 시 로그인 유저가 없으면 예외가 발생한다.")
    void upload_userNotFoundThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.upload(1L, postRequest("title", "body", "")))
                .isInstanceOf(NotRegisteredException.class);

        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("삭제되지 않은 게시글 목록을 조회한다.")
    void getPostList_returnsPosts() {
        when(postRepository.findByStatusNot(PostStatus.DELETED)).thenReturn(List.of(post));

        List<PostListResponseDTO> result = postService.getPostList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor().getNickname()).isEqualTo("tester");
        assertThat(result.get(0).getPost().getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("블라인드 게시글은 숨김 제목으로 목록에 반환된다.")
    void getPostList_blindedPostReturnsHiddenTitle() {
        post.blindPost();
        when(postRepository.findByStatusNot(PostStatus.DELETED)).thenReturn(List.of(post));

        List<PostListResponseDTO> result = postService.getPostList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPost().getTitle()).isEqualTo("숨김 처리된 게시글");
    }

    @Test
    @DisplayName("게시글 상세를 조회하고 조회수를 증가시킨다.")
    void getPostDetail_returnsDetailAndIncreaseViews() {
        when(postRepository.findByPostId(1L)).thenReturn(Optional.of(post));

        PostDetailResponseDTO response = postService.getPostDetail(UserRole.ROLE_USER, 1L);

        assertThat(response.getAuthor().getNickname()).isEqualTo("tester");
        assertThat(response.getPost().getTitle()).isEqualTo("title");
        assertThat(response.getMeta().getViews()).isEqualTo(1);
        assertThat(post.getViews()).isEqualTo(1);
    }

    @Test
    @DisplayName("없는 게시글 상세 조회 시 예외가 발생한다.")
    void getPostDetail_notFoundThrowsException() {
        when(postRepository.findByPostId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostDetail(UserRole.ROLE_USER, 1L))
                .isInstanceOf(ContentNotFoundException.class);
    }

    @Test
    @DisplayName("삭제된 게시글 상세 조회 시 예외가 발생한다.")
    void getPostDetail_deletedPostThrowsException() {
        post.deletePost();
        when(postRepository.findByPostId(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.getPostDetail(UserRole.ROLE_USER, 1L))
                .isInstanceOf(ContentNotFoundException.class);
    }

    @Test
    @DisplayName("일반 유저는 블라인드 게시글 상세 조회 시 예외가 발생한다.")
    void getPostDetail_blindedPost_userThrowsForbidden() {
        post.blindPost();
        when(postRepository.findByPostId(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.getPostDetail(UserRole.ROLE_USER, 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("관리자는 블라인드 게시글 상세 조회가 가능하다.")
    void getPostDetail_blindedPost_adminCanAccess() {
        post.blindPost();
        when(postRepository.findByPostId(1L)).thenReturn(Optional.of(post));

        PostDetailResponseDTO response = postService.getPostDetail(UserRole.ROLE_ADMIN, 1L);

        assertThat(response.getPost().getPostId()).isEqualTo(1L);
        assertThat(response.getPost().getTitle()).isEqualTo("title");
        assertThat(response.getAuthor().getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("작성자는 게시글을 수정할 수 있다.")
    void modifyPost_ownerCanModify() {
        PostRequestDTO request = postRequest("new title", "new body", "");

        when(postRepository.findByPostId(1L)).thenReturn(Optional.of(post));

        PostDetailResponseDTO response = postService.modifyPost(1L, 1L, request);

        assertThat(response.getPost().getTitle()).isEqualTo("new title");
        assertThat(response.getPost().getPostBody()).isEqualTo("new body");
        assertThat(response.getPost().getRevision()).isEqualTo(2);
        verify(postRevisionRepository).save(any(PostRevision.class));
    }

    @Test
    @DisplayName("작성자가 아니면 게시글을 수정할 수 없다.")
    void modifyPost_notOwnerThrowsForbidden() {
        when(postRepository.findByPostId(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.modifyPost(2L, 1L, postRequest("new", "body", "")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("작성자는 게시글을 삭제할 수 있다.")
    void deletePost_ownerCanDelete() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L, 1L);

        assertThat(post.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("작성자가 아니면 게시글을 삭제할 수 없다.")
    void deletePost_notOwnerThrowsForbidden() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(2L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }
    // 테스트용 postRequestDTO 생성기.
    private PostRequestDTO postRequest(String title, String body, String imageUrl) {
        PostRequestDTO request = new PostRequestDTO();
        request.setTitle(title);
        request.setPostBody(body);
        request.setPostImageUrl(imageUrl);
        return request;
    }
}