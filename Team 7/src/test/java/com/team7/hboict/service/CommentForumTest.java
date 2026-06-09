package com.team7.hboict.service;

import com.team7.hboict.model.CommentForum;
import com.team7.hboict.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void happyFlow_createComment_savesAndReturnsComment() {
        // Arrange
        CommentForum comment = new CommentForum(1L, "user@mail.com", "Nice post");

        CommentForum savedComment = new CommentForum(1L, "user@mail.com", "Nice post");

        when(commentRepository.save(comment)).thenReturn(savedComment);

        // Act
        CommentForum result = commentService.createComment(comment);

        // Assert
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getAuthor()).isEqualTo("user@mail.com");
        assertThat(result.getDescription()).isEqualTo("Nice post");
        assertThat(result.getCreatedAt()).isNotNull();

        verify(commentRepository).save(comment);
    }

    @Test
    void unhappyFlow_createComment_repositoryThrowsException() {
        // Arrange
        CommentForum comment = new CommentForum(1L, "user@mail.com", "Nice post");

        when(commentRepository.save(comment))
                .thenThrow(new RuntimeException("Database error"));

        // Act + Assert
        assertThatThrownBy(() -> commentService.createComment(comment))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(commentRepository).save(comment);
    }

    @Test
    void happyFlow_getCommentsByPostId_returnsComments() {
        // Arrange
        CommentForum comment = new CommentForum(1L, "user@mail.com", "Nice post");

        when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));

        // Act
        List<CommentForum> result = commentService.getCommentsByPostId(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPostId()).isEqualTo(1L);
        assertThat(result.get(0).getAuthor()).isEqualTo("user@mail.com");
        assertThat(result.get(0).getDescription()).isEqualTo("Nice post");

        verify(commentRepository).findByPostId(1L);
    }

    @Test
    void unhappyFlow_getCommentsByPostId_returnsEmptyListWhenNoCommentsExist() {
        // Arrange
        when(commentRepository.findByPostId(1L)).thenReturn(List.of());

        // Act
        List<CommentForum> result = commentService.getCommentsByPostId(1L);

        // Assert
        assertThat(result).isEmpty();

        verify(commentRepository).findByPostId(1L);
    }

    @Test
    void unhappyFlow_getCommentsByPostId_repositoryThrowsException() {
        // Arrange
        when(commentRepository.findByPostId(1L))
                .thenThrow(new RuntimeException("Database error"));

        // Act + Assert
        assertThatThrownBy(() -> commentService.getCommentsByPostId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(commentRepository).findByPostId(1L);
    }

    @Test
    void happyFlow_deleteComment_deletesCommentById() {
        // Arrange
        Long commentId = 10L;

        // Act
        commentService.deleteComment(commentId);

        // Assert
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void unhappyFlow_deleteComment_repositoryThrowsException() {
        // Arrange
        Long commentId = 10L;

        doThrow(new RuntimeException("Database error"))
                .when(commentRepository)
                .deleteById(commentId);

        // Act + Assert
        assertThatThrownBy(() -> commentService.deleteComment(commentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");

        verify(commentRepository).deleteById(commentId);
    }
}