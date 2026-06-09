package com.team7.hboict.api;

import com.team7.hboict.model.ForumPost;
import com.team7.hboict.dto.CreatePostRequest;
import com.team7.hboict.service.AuthService;
import com.team7.hboict.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/forum-api")
@Tag(name = "Forum Posts", description = "Operations related to forum posts")
public class ForumController {

    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);

    private final ForumService forumService;
    private final AuthService authService;

    public ForumController(ForumService forumService, AuthService authService) {
        this.forumService = forumService;
        this.authService = authService;
    }

    @PostMapping("/posts-api")
    @Operation(
            summary = "Create a forum post",
            description = "Creates a new forum post."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forum post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid forum post data")
    })
    public ForumPost createPost(@Valid @RequestBody CreatePostRequest request, HttpServletRequest httpRequest) {
        logger.info("createPost called");

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized forum post creation attempt - missing or invalid Authorization header");
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Login required");
        }

        try {
            String email = authService.getEmailFromToken(authHeader.substring(7));
            logger.debug("Forum post payload received with title={}, author={}", request.getTitle(), email);

            ForumPost post = new ForumPost(request.getTitle(), email, request.getDescription());
            logger.debug("ForumPost object created: title={}, author={}, description length={}",
                    post.getTitle(), post.getAuthor(), post.getDescription() == null ? 0 : post.getDescription().length());

            ForumPost savedPost = forumService.createPost(post);
            logger.info("Forum post created successfully with id={}", savedPost.getId());
            return savedPost;
        } catch (Exception e) {
            logger.error("Error creating forum post", e);
            throw e;
        }
    }

    @GetMapping("/posts-api")
    @Operation(
            summary = "Retrieve forum posts",
            description = "Returns a paginated list of forum posts."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forum posts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    public Page<ForumPost> getPosts(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of posts per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Fetching forum posts page={} size={}", page, size);

        Page<ForumPost> posts = forumService.getPosts(page, size);

        logger.debug("Fetched {} forum posts (page {})", posts.getNumberOfElements(), posts.getNumber());
        return posts;
    }

    @GetMapping("/posts-api/mine")
    public List<ForumPost> getMyPosts(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        return forumService.getPostsByAuthor(authentication.getName());
    }

    @PutMapping("/posts-api/{id}")
    @Operation(
            summary = "Update a forum post",
            description = "Updates an existing forum post. Only administrators are allowed to perform this operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forum post updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid forum post data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Admin privileges required"),
            @ApiResponse(responseCode = "404", description = "Forum post not found")
    })
    public ForumPost updatePost(
            @Parameter(description = "Unique identifier of the forum post", example = "1")
            @PathVariable Long id,

            @Valid @RequestBody ForumPost post,
            HttpServletRequest request
    ) {
        logger.info("Update requested for forum post id={}", id);

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized forum post update attempt for id={}", id);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        String email = authService.getEmailFromToken(authHeader.substring(7));

        if (!authService.isAdmin(email)) {
            logger.warn("Forbidden forum post update attempt for id={} by email={}", id, email);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }

        ForumPost updatedPost = forumService.updatePost(id, post);

        if (updatedPost == null) {
            logger.warn("Forum post not found for update id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Forum post not found");
        }

        logger.info("Forum post updated successfully id={} by admin={}", id, email);

        return updatedPost;
    }

    @DeleteMapping("/posts-api/{id}")
    public void deletePost(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        String email = authentication.getName();
        forumService.deletePost(id, email, authService.isAdmin(email));
    }
}
