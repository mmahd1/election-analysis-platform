package com.team7.hboict.service;

import com.team7.hboict.dto.LoginRequest;
import com.team7.hboict.dto.RegisterRequest;
import com.team7.hboict.model.User;
import com.team7.hboict.repository.UserRepository;
import com.team7.hboict.security.JwtService;
import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final String ADMIN_EMAIL = "Marouan@admin.nl";

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public AuthService(UserRepository userRepository, JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {
        User user;

        try {
            user =
                    userRepository.findByEmail(
                            request.getEmail()
                    );
        } catch (NoResultException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }

        if (user == null) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }

        boolean passwordCorrect = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordCorrect) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public void register(RegisterRequest request) {
        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is al in gebruik"
            );
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userRepository.save(user);
    }

    public String getEmailFromToken(String token) {
        return jwtService.extractEmail(token);
    }

    public User getUserFromToken(String token) {
        String email = getEmailFromToken(token);

        return userRepository.findByEmail(email);
    }

    public boolean isAdmin(String email) {
        return email != null && email.equalsIgnoreCase(ADMIN_EMAIL);
    }

}
