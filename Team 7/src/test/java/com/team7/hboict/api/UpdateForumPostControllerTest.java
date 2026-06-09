package com.team7.hboict.api;

import com.team7.hboict.model.ForumPost;
import com.team7.hboict.service.AuthService;
import com.team7.hboict.service.ForumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UpdateForumPostControllerTest {

    @Mock
    private ForumService forumService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ForumController forumController;

    /**
     * Verifies that an authenticated admin user can update a forum post and
     * receives the updated post body in the response.
     */
    @Test
    public void happyFlow_updatePost_returnsUpdatedForumPost() throws Exception {
        ForumPost updatedPost = new ForumPost("Nieuwe titel", "Admin", "Nieuwe inhoud");

        when(forumService.updatePost(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(ForumPost.class)))
                .thenReturn(updatedPost);
        when(authService.getEmailFromToken("token")).thenReturn("Marouan@admin.nl");
        when(authService.isAdmin("Marouan@admin.nl")).thenReturn(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();

        mockMvc.perform(put("/forum-api/posts-api/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nieuwe titel\",\"author\":\"Admin\",\"description\":\"Nieuwe inhoud\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nieuwe titel"))
                .andExpect(jsonPath("$.description").value("Nieuwe inhoud"));

        verify(authService).getEmailFromToken("token");
        verify(authService).isAdmin("Marouan@admin.nl");
        verify(forumService).updatePost(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any(ForumPost.class));
        verifyNoMoreInteractions(forumService, authService);
    }

    /**
     * Verifies that the controller returns HTTP 404 when an admin tries to
     * update a forum post that does not exist.
     */
    @Test
    public void unhappyFlow_updatePost_returnsNotFoundWhenPostDoesNotExist() throws Exception {
        when(forumService.updatePost(org.mockito.ArgumentMatchers.eq(99L), org.mockito.ArgumentMatchers.any(ForumPost.class)))
                .thenReturn(null);
        when(authService.getEmailFromToken("token")).thenReturn("Marouan@admin.nl");
        when(authService.isAdmin("Marouan@admin.nl")).thenReturn(true);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();

        mockMvc.perform(put("/forum-api/posts-api/99")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Nieuwe titel\",\"author\":\"Admin\",\"description\":\"Nieuwe inhoud\"}"))
                .andExpect(status().isNotFound());

        verify(authService).getEmailFromToken("token");
        verify(authService).isAdmin("Marouan@admin.nl");
        verify(forumService).updatePost(org.mockito.ArgumentMatchers.eq(99L), org.mockito.ArgumentMatchers.any(ForumPost.class));
        verifyNoMoreInteractions(forumService, authService);
    }

    /**
     * Verifies that a non-admin user receives HTTP 403 and cannot reach the
     * forum update service logic.
     */
    @Test
    public void unhappyFlow_updatePost_returnsForbiddenWhenUserIsNotAdmin() throws Exception {
        when(authService.getEmailFromToken("token")).thenReturn("user@test.nl");
        when(authService.isAdmin("user@test.nl")).thenReturn(false);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();

        mockMvc.perform(put("/forum-api/posts-api/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nieuwe titel\",\"author\":\"Admin\",\"description\":\"Nieuwe inhoud\"}"))
                .andExpect(status().isForbidden());

        verify(authService).getEmailFromToken("token");
        verify(authService).isAdmin("user@test.nl");
        verifyNoInteractions(forumService);
    }

    /**
     * Verifies that the controller returns HTTP 401 when no Authorization
     * header is supplied for an admin-only update request.
     */
    @Test
    public void unhappyFlow_updatePost_returnsUnauthorizedWhenAuthorizationHeaderIsMissing() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();

        mockMvc.perform(put("/forum-api/posts-api/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nieuwe titel\",\"author\":\"Admin\",\"description\":\"Nieuwe inhoud\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authService, forumService);
    }

    /**
     * Verifies that the controller returns HTTP 401 when the Authorization
     * header uses an unsupported authentication scheme.
     */
    @Test
    public void unhappyFlow_updatePost_returnsUnauthorizedWhenAuthorizationHeaderHasWrongScheme() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();

        mockMvc.perform(put("/forum-api/posts-api/1")
                        .header("Authorization", "Token abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nieuwe titel\",\"author\":\"Admin\",\"description\":\"Nieuwe inhoud\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(authService, forumService);
    }

    /**
     * Verifies that the controller returns HTTP 400 when the update payload
     * violates validation rules, such as an empty title.
     */
    @Test
    public void unhappyFlow_updatePost_returnsBadRequestWhenPayloadIsInvalid() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(forumController).build();

        mockMvc.perform(put("/forum-api/posts-api/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\"Admin\",\"description\":\"Nieuwe inhoud\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService, forumService);
    }
}
