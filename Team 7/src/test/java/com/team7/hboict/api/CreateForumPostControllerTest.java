package com.team7.hboict.api;

import com.team7.hboict.model.ForumPost;
import com.team7.hboict.service.AuthService;
import com.team7.hboict.service.ForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CreateForumPostControllerTest {

    @Mock
    private ForumService forumService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ForumController forumController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();
    }

    private String json(String title, String description) {
        return """
                {"title": "%s",
                  "description": "%s"}
                """.formatted(title, description);
    }

    // HAPPY FLOW

    @Test
    void shouldCreatePostSuccessfully() throws Exception {

        String token = "token";
        String email = "user@test.com";

        when(authService.getEmailFromToken(token)).thenReturn(email);
        when(forumService.createPost(any(ForumPost.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/forum-api/posts-api")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Titel", "Beschrijving")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titel"))
                .andExpect(jsonPath("$.author").value(email));

        verify(authService).getEmailFromToken(token);
        verify(forumService).createPost(any(ForumPost.class));
    }

    // UNHAPPY FLOW - AUTH

    @Test
    void shouldReturn401WhenNoAuthHeader() throws Exception {

        mockMvc.perform(post("/forum-api/posts-api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Titel", "Beschrijving")))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authService, forumService);
    }

    // UNHAPPY FLOW - VALIDATION

    @Test
    void shouldReturn400WhenTitleMissing() throws Exception {

        mockMvc.perform(post("/forum-api/posts-api")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"description": "test"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService, forumService);
    }

    @Test
    void shouldReturn400WhenDescriptionEmpty() throws Exception {

        mockMvc.perform(post("/forum-api/posts-api")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Titel", "")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService, forumService);
    }
}