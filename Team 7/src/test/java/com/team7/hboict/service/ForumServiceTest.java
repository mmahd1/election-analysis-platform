package com.team7.hboict.service;

import com.team7.hboict.model.ForumPost;
import com.team7.hboict.repository.ForumPostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForumServiceTest {

    @Mock
    private ForumPostRepository forumPostRepository;

    @InjectMocks
    private ForumService forumService;

    @Test
    public void testCreatePost() {

        // arrange
        ForumPost post = new ForumPost(
                "Test title",
                "test@example.com",
                "Test description"
        );

        when(forumPostRepository.save(post)).thenReturn(post);

        // act
        ForumPost result = forumService.createPost(post);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test title");
        assertThat(result.getAuthor()).isEqualTo("test@example.com");
        assertThat(result.getDescription()).isEqualTo("Test description");

        verify(forumPostRepository, times(1)).save(post);
    }
}