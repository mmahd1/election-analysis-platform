package com.team7.hboict.service;

import com.team7.hboict.model.ForumPost;
import com.team7.hboict.repository.CommentRepository;
import com.team7.hboict.repository.ForumPostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ForumService {
    private final ForumPostRepository forumPostRepository;
    private final CommentRepository commentRepository;

    public ForumService(ForumPostRepository forumPostRepository, CommentRepository commentRepository) {
        this.forumPostRepository = forumPostRepository;
        this.commentRepository = commentRepository;
    }

    public ForumPost createPost(ForumPost post) {
        return forumPostRepository.save(post);
    }

    public Page<ForumPost> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return forumPostRepository.findPage(pageable);
    }

    public List<ForumPost> getPostsByAuthor(String author) {
        return forumPostRepository.findByAuthor(author);
    }

    public ForumPost updatePost(Long id, ForumPost updatedPost) {
        ForumPost existingPost = forumPostRepository.findById(id);

        if (existingPost == null) {
            return null;
        }

        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setDescription(updatedPost.getDescription());

        return forumPostRepository.save(existingPost);
    }

    public void deletePost(Long id, String requesterEmail, boolean isAdmin) {
        ForumPost existingPost = forumPostRepository.findById(id);

        if (existingPost == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Forum post not found");
        }

        if (requesterEmail == null || requesterEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        if (!isAdmin && (existingPost.getAuthor() == null
                || !existingPost.getAuthor().equalsIgnoreCase(requesterEmail))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own post");
        }

        commentRepository.deleteByPostId(id);
        forumPostRepository.deleteById(id);
    }
}
