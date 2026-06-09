package com.team7.hboict.service;

import com.team7.hboict.dto.LoginRequest;
import com.team7.hboict.model.User;
import com.team7.hboict.repository.UserRepository;
import com.team7.hboict.security.JwtService;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests voor AuthService.
 *
 * Deze testklasse controleert de core business logic van authenticatie:
 * - succesvolle login
 * - fout wachtwoord
 * - gebruiker niet gevonden
 * - JWT email extractie
 *
 * Externe dependencies (database + JWT service) worden gemockt
 * zodat alleen de logica van AuthService wordt getest.
 *
 * @author Edris Zanikhil
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - gebruiker bestaat
     * - wachtwoord is correct
     *
     * Verwachting:
     * - JWT token wordt gegenereerd
     * - token wordt teruggegeven
     */
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.nl");
        request.setPassword("password123");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("test@test.nl");

        // Simuleert opgeslagen gehashte wachtwoord in database
        user.setPassword(encoder.encode("password123"));

        // Mock: gebruiker gevonden in database
        when(userRepository.findByEmail("test@test.nl"))
                .thenReturn(user);

        // Mock: JWT generatie
        when(jwtService.generateToken("test@test.nl"))
                .thenReturn("jwt-token");

        String result = authService.login(request);

        // Controle: juiste token wordt teruggegeven
        assertEquals("jwt-token", result);

        // Controle: repository wordt gebruikt
        verify(userRepository).findByEmail("test@test.nl");

        // Controle: JWT service wordt aangeroepen
        verify(jwtService).generateToken("test@test.nl");
    }

    /**
     * UNHAPPY FLOW
     *
     * Situatie:
     * - gebruiker bestaat niet in database
     *
     * Verwachting:
     * - 401 Unauthorized exception
     * - geen JWT generatie
     */
    @Test
    void login_ShouldThrowUnauthorized_WhenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.nl");
        request.setPassword("password123");

        // Mock: geen gebruiker gevonden
        when(userRepository.findByEmail("test@test.nl"))
                .thenThrow(new NoResultException());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> authService.login(request));

        // Controle: juiste HTTP status
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        // Controle: JWT mag niet worden aangeroepen
        verifyNoInteractions(jwtService);
    }

    /**
     * UNHAPPY FLOW
     *
     * Situatie:
     * - gebruiker bestaat
     * - wachtwoord is fout
     *
     * Verwachting:
     * - 401 Unauthorized exception
     * - geen JWT token wordt gegenereerd
     */
    @Test
    void login_ShouldThrowUnauthorized_WhenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.nl");
        request.setPassword("wrong-password");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail("test@test.nl");

        // Correct opgeslagen wachtwoord
        user.setPassword(encoder.encode("password123"));

        when(userRepository.findByEmail("test@test.nl"))
                .thenReturn(user);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> authService.login(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());

        // JWT mag niet worden aangeroepen
        verify(jwtService, never()).generateToken(anyString());
    }

    /**
     * JWT HELPER TEST
     *
     * Controleert of email correct uit token wordt gehaald.
     */
    @Test
    void getEmailFromToken_ShouldReturnEmail() {

        when(jwtService.extractEmail("token123"))
                .thenReturn("test@test.nl");

        String result = authService.getEmailFromToken("token123");

        assertEquals("test@test.nl", result);

        verify(jwtService).extractEmail("token123");
    }
}
