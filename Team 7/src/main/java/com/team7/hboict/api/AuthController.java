package com.team7.hboict.api;

import com.team7.hboict.dto.LoginRequest;
import com.team7.hboict.dto.RegisterRequest;
import com.team7.hboict.dto.UserResponse;
import com.team7.hboict.model.User;
import com.team7.hboict.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller responsible for authentication-related operations.
 *
 * <p>This controller handles user registration, login, and retrieval of the
 * currently authenticated user using JWT Bearer tokens.</p>
 *
 * <p><b>Base URL:</b> /api/auth</p>
 *
 * @author Edris Zanikhil
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for login, registration and retrieving the current user")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Constructor injection of AuthService.
     *
     * @param authService service responsible for authentication logic and JWT handling
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    @Operation(
            summary = "Login user",
            description = "Authenticates a user using email and password and returns a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {

        logger.info("Login attempt for email={}", request.getEmail());

        String token = authService.login(request);

        logger.info("Login successful for email={}", request.getEmail());

        return token;
    }

    /**
     * Retrieves the currently authenticated user.
     */
    @Operation(
            summary = "Get current authenticated user",
            description = "Returns user information based on the JWT token in the Authorization header."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(
            @Parameter(description = "JWT Bearer token in format: Bearer <token>")
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized access attempt to /me endpoint");
            return ResponseEntity.status(401).build();
        }

        String token = authHeader.substring(7);

        User user = authService.getUserFromToken(token);

        logger.info("Fetched authenticated user: {}", user.getEmail());

        return ResponseEntity.ok(
                new UserResponse(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        authService.isAdmin(user.getEmail())
                )
        );
    }

    /**
     * Registers a new user.
     */
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with email and password."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {

        logger.info("Register attempt for email={}", request.getEmail());

        authService.register(request);

        logger.info("User registered successfully: {}", request.getEmail());

        return ResponseEntity.ok().build();
    }
}
