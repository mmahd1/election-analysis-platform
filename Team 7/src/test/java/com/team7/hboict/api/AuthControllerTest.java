package com.team7.hboict.api;

import com.team7.hboict.dto.LoginRequest;
import com.team7.hboict.dto.RegisterRequest;
import com.team7.hboict.model.User;
import com.team7.hboict.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - gebruiker logt in met geldige gegevens
     *
     * Verwachting:
     * - controller geeft het JWT-token terug
     */
    @Test
    public void login_ShouldReturnToken_WhenCredentialsAreCorrect() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn("jwt-token");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"sophie.devries@example.nl","password":"Welkom123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }

    /**
     * UNHAPPY FLOW
     *
     * Situatie:
     * - gebruiker logt in met ongeldige gegevens
     *
     * Verwachting:
     * - controller geeft 401 Unauthorized terug
     */
    @Test
    public void login_ShouldReturnUnauthorized_WhenCredentialsAreIncorrect() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"sophie.devries@example.nl","password":"verkeerd-wachtwoord"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - nieuwe gebruiker verstuurt geldige registratiegegevens
     *
     * Verwachting:
     * - controller geeft 200 OK terug
     */
    @Test
    public void register_ShouldReturnOk_WhenRegistrationIsValid() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName":"Sophie",
                                  "lastName":"de Vries",
                                  "email":"sophie.devries@example.nl",
                                  "password":"Welkom123"
                                }
                                """))
                .andExpect(status().isOk());

        verify(authService).register(any(RegisterRequest.class));
    }

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - ingelogde gebruiker vraagt het eigen profiel op
     *
     * Verwachting:
     * - controller geeft de gegevens van de gebruiker terug
     */
    @Test
    public void me_ShouldReturnCurrentUser_WhenBearerTokenIsValid() throws Exception {
        User user = new User();
        user.setEmail("sophie.devries@example.nl");
        user.setFirstName("Sophie");
        user.setLastName("de Vries");
        when(authService.getUserFromToken("jwt-token")).thenReturn(user);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("sophie.devries@example.nl"))
                .andExpect(jsonPath("$.firstName").value("Sophie"))
                .andExpect(jsonPath("$.lastName").value("de Vries"));

        verify(authService).getUserFromToken("jwt-token");
    }

    /**
     * UNHAPPY FLOW
     *
     * Situatie:
     * - gebruiker vraagt het profiel op zonder Bearer-token
     *
     * Verwachting:
     * - controller geeft 401 Unauthorized terug
     */
    @Test
    public void me_ShouldReturnUnauthorized_WhenBearerTokenIsMissing() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
