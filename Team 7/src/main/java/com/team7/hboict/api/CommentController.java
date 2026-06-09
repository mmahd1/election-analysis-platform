package com.team7.hboict.api;

import com.team7.hboict.dto.CreateCommentRequest;
import com.team7.hboict.model.CommentForum;
import com.team7.hboict.service.CommentService;
import com.team7.hboict.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST-controller voor comments op forum posts.
 */
@RestController
@RequestMapping("/forum-api/posts/{postId}/comments")
@Tag(name = "Forum Comments", description = "Endpoints voor comments op forum posts")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;
    private final AuthService authService;

    /**
     * Maakt een nieuwe CommentController aan.
     *
     * @param commentService service voor comment-logica
     */
    public CommentController(CommentService commentService, AuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    /**
     * Haalt alle comments op van een specifieke post.
     *
     * @param postId id van de forum post
     * @return lijst met comments
     */
    @Operation(summary = "Get comments", description = "Haalt alle comments op van een forum post.")
    @ApiResponse(responseCode = "200", description = "Comments succesvol opgehaald")
    @GetMapping
    public List<CommentForum> getComments(
            @Parameter(description = "Id van de forum post", example = "1")
            @PathVariable Long postId
    ) {
        logger.info("Fetching comments for post {}", postId);

        List<CommentForum> comments = commentService.getCommentsByPostId(postId);

        logger.debug("Found {} comments for post {}", comments.size(), postId);

        return comments;
    }

    /**
     * Maakt een nieuwe comment aan.
     *
     * @param postId id van de forum post
     * @param request request body met commenttekst
     * @param authentication ingelogde gebruiker
     * @return aangemaakte comment
     */
    @Operation(summary = "Create comment", description = "Maakt een comment aan voor een forum post.")
    @ApiResponse(responseCode = "200", description = "Comment succesvol aangemaakt")
    @ApiResponse(responseCode = "401", description = "Gebruiker is niet ingelogd")
    @PostMapping

    public CommentForum createComment(
            @Parameter(description = "Id van de forum post", example = "1")
            @PathVariable Long postId,

            @Valid @RequestBody CreateCommentRequest request,

            @Parameter(hidden = true)
            Authentication authentication
    ) {
        logger.info("Attempting to create comment for post {}", postId);

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Unauthorized comment creation attempt for post {}", postId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        String authorEmail = authentication.getName();

        logger.debug(
                "User {} is creating a comment for post {} with description length {}",
                authorEmail,
                postId,
                request.getDescription().length()
        );

        try {
            CommentForum newComment = new CommentForum(postId, authorEmail, request.getDescription());
            CommentForum savedComment = commentService.createComment(newComment);

            logger.info("Comment {} created successfully for post {}", savedComment.getId(), postId);

            return savedComment;
        } catch (Exception e) {
            logger.error("Failed to create comment for post {}", postId, e);
            throw e;
        }
    }

    /**
     * @author: Edris
     */
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication authentication) {
        logger.info("Delete requested for comment id={} on post={}", commentId, postId);

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            logger.warn("Unauthorized comment delete attempt for id={}", commentId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        String email = authentication.getName();

        CommentForum existing = commentService.getCommentById(commentId);
        if (existing == null) {
            logger.warn("Comment not found for delete id={}", commentId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

        boolean isOwner = email != null && email.equalsIgnoreCase(existing.getAuthor());
        if (!isOwner && !authService.isAdmin(email)) {
            logger.warn("Forbidden comment delete attempt for id={} by email={}", commentId, email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not owner or admin");
        }

        commentService.deleteComment(commentId);
        logger.info("Comment deleted successfully id={} by email={}", commentId, email);
    }
}
