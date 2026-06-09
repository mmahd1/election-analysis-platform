package com.team7.hboict.service;

import com.team7.hboict.repository.UserRepository;
import com.team7.hboict.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceAdminTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                org.mockito.Mockito.mock(UserRepository.class),
                org.mockito.Mockito.mock(JwtService.class)
        );
    }

    /**
     * Verifies that the exact configured admin email is recognized as admin.
     */
    @Test
    void isAdmin_ShouldReturnTrue_ForAdminEmail() {
        assertTrue(authService.isAdmin("Marouan@admin.nl"));
    }

    /**
     * Verifies that the fixed admin email check is case-insensitive.
     */
    @Test
    void isAdmin_ShouldReturnTrue_IgnoringEmailCase() {
        assertTrue(authService.isAdmin("marouan@ADMIN.nl"));
    }

    /**
     * Verifies that an unrelated email address is rejected as non-admin.
     */
    @Test
    void isAdmin_ShouldReturnFalse_ForDifferentEmail() {
        assertFalse(authService.isAdmin("user@test.nl"));
    }

    /**
     * Verifies that an email address that only resembles the configured
     * admin email is still rejected as a non-admin user.
     */
    @Test
    void isAdmin_ShouldReturnFalse_ForSimilarButDifferentAdminEmail() {
        assertFalse(authService.isAdmin("marouan-admin.nl"));
    }

    /**
     * Verifies that surrounding whitespace prevents a match with the fixed
     * admin email and therefore returns false.
     */
    @Test
    void isAdmin_ShouldReturnFalse_WhenEmailContainsLeadingOrTrailingWhitespace() {
        assertFalse(authService.isAdmin(" Marouan@admin.nl "));
    }

    /**
     * Verifies that null input is safely treated as a non-admin value.
     */
    @Test
    void isAdmin_ShouldReturnFalse_ForNullEmail() {
        assertFalse(authService.isAdmin(null));
    }
}
