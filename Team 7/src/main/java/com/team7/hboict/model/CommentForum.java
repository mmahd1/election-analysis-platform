package com.team7.hboict.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Model/Entity voor een comment op een forum post.
 */
@Entity
public class CommentForum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long postId;

    private String author;

    @Column(length = 2000)
    private String description;

    private LocalDateTime createdAt;

    // JPA only
    protected CommentForum() {
    }

    public CommentForum(Long postId, String author, String description) {
        this.postId = postId;
        this.author = author;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    @NotBlank
    public String getAuthor() {
        return author;
    }

    @NotBlank
    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

