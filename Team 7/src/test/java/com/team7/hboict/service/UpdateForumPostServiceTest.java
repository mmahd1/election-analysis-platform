package com.team7.hboict.service;

import com.team7.hboict.model.ForumPost;
import com.team7.hboict.repository.ForumPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateForumPostServiceTest {

    @Mock
    private ForumPostRepository forumPostRepository;

    @InjectMocks
    private ForumService forumService;

    /**
     * Verifies that the service updates the mutable fields of an existing
     * forum post and returns the saved result.
     */
    @Test
    public void happyFlow_updatePost_updatesExistingAdminPost() {
        ForumPost existingPost = new ForumPost("Oude titel", "Admin", "Oude inhoud");
        ForumPost updatedPost = new ForumPost("Nieuwe titel", "Admin", "Nieuwe inhoud");

        when(forumPostRepository.findById(1L)).thenReturn(existingPost);
        when(forumPostRepository.save(existingPost)).thenReturn(existingPost);

        ForumPost result = forumService.updatePost(1L, updatedPost);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Nieuwe titel");
        assertThat(result.getDescription()).isEqualTo("Nieuwe inhoud");
    }

    /**
     * Verifies that the service returns null when the target forum post id
     * cannot be found in the repository.
     */
    @Test
    public void unhappyFlow_updatePost_returnsNullWhenPostDoesNotExist() {
        ForumPost updatedPost = new ForumPost("Nieuwe titel", "Admin", "Nieuwe inhoud");

        when(forumPostRepository.findById(99L)).thenReturn(null);

        ForumPost result = forumService.updatePost(99L, updatedPost);

        assertThat(result).isNull();
    }
}
