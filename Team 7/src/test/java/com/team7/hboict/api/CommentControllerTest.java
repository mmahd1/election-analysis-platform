package com.team7.hboict.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team7.hboict.dto.CreateCommentRequest;
import com.team7.hboict.model.CommentForum;
import com.team7.hboict.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verifies that the controller returns all comments for a specific forum post
     * and serializes the comment fields correctly.
     */
    @Test
    public void happyFlow_getComments_returnsCommentsForPost() throws Exception {
        CommentForum comment = new CommentForum(1L, "user@mail.com", "Nice post");

        when(commentService.getCommentsByPostId(1L))
                .thenReturn(List.of(comment));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        mockMvc.perform(get("/forum-api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[0].author").value("user@mail.com"))
                .andExpect(jsonPath("$[0].description").value("Nice post"));

        verify(commentService).getCommentsByPostId(1L);
        verifyNoMoreInteractions(commentService);
    }

    /**
     * Verifies that the controller returns an empty JSON array when a post has
     * no comments.
     */
    @Test
    public void unhappyFlow_getComments_returnsEmptyListWhenNoCommentsExist() throws Exception {
        when(commentService.getCommentsByPostId(1L))
                .thenReturn(List.of());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        mockMvc.perform(get("/forum-api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(commentService).getCommentsByPostId(1L);
        verifyNoMoreInteractions(commentService);
    }

    /**
     * Verifies that an authenticated user can create a comment and receives the
     * created comment in the response.
     */
    @Test
    public void happyFlow_createComment_returnsCreatedComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setDescription("This is a comment");

        CommentForum savedComment = new CommentForum(
                1L,
                "user@mail.com",
                "This is a comment"
        );

        when(commentService.createComment(any(CommentForum.class)))
                .thenReturn(savedComment);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user@mail.com", null);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        mockMvc.perform(post("/forum-api/posts/1/comments")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.author").value("user@mail.com"))
                .andExpect(jsonPath("$.description").value("This is a comment"));

        verify(commentService).createComment(any(CommentForum.class));
        verifyNoMoreInteractions(commentService);
    }

    /**
     * Verifies that the controller returns HTTP 401 when a user tries to create
     * a comment without being logged in.
     */
    @Test
    public void unhappyFlow_createComment_returnsUnauthorizedWhenUserIsNotLoggedIn() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setDescription("This is a comment");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        mockMvc.perform(post("/forum-api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).createComment(any(CommentForum.class));
        verifyNoMoreInteractions(commentService);
    }

    /**
     * Verifies that the controller returns HTTP 400 when the comment payload is
     * invalid, for example when the description is empty.
     */
    @Test
    public void unhappyFlow_createComment_returnsBadRequestWhenPayloadIsInvalid() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setDescription("");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("user@mail.com", null);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        mockMvc.perform(post("/forum-api/posts/1/comments")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(commentService);
    }

    /**
     * Verifies that the controller calls the service to delete a comment and
     * returns HTTP 200 when deletion succeeds.
     */
    @Test
    public void happyFlow_deleteComment_deletesComment() throws Exception {
        Long postId = 1L;
        Long commentId = 10L;

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        mockMvc.perform(delete("/forum-api/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk());

        verify(commentService).deleteComment(commentId);
        verifyNoMoreInteractions(commentService);
    }
}