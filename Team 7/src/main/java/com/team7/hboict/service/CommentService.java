package com.team7.hboict.service;

import com.team7.hboict.model.CommentForum;
import com.team7.hboict.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviceklasse voor de businesslogica van forum comments.
 */
@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    /**
     * Maakt een nieuwe CommentService aan.
     *  
     * @param commentRepository repository voor database-operaties van comments
     */
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Slaat een nieuwe comment op.
     *
     * @param comment comment die opgeslagen moet worden
     * @return opgeslagen comment
     */
    public CommentForum createComment(CommentForum comment) {
        logger.info("Saving comment for post {}", comment.getPostId());

        CommentForum savedComment = commentRepository.save(comment);

        logger.debug("Saved comment with id {}", savedComment.getId());

        return savedComment;
    }

    /**
     * Haalt alle comments op voor een specifieke post.
     *
     * @param postId id van de forum post
     * @return lijst met comments
     */
    public List<CommentForum> getCommentsByPostId(Long postId) {
        logger.info("Retrieving comments for post {}", postId);

        List<CommentForum> comments = commentRepository.findByPostId(postId);

        if (comments.isEmpty()) {
            logger.warn("No comments found for post {}", postId);
        }

        return comments;
    }

    public CommentForum getCommentById(Long id) {
        logger.info("Retrieving comment by id {}", id);
        return commentRepository.findById(id);
    }

    /**
     * Verwijdert een comment.
     *
     * @param commentId id van de comment
     */
    public void deleteComment(Long commentId) {
        logger.info("Deleting comment with id {}", commentId);
        commentRepository.deleteById(commentId);
    }
}