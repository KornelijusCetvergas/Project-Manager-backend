package lt.ca.javau12.todolist.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import lt.ca.javau12.todolist.dto.LoginResponse;
import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.enums.Role;
import lt.ca.javau12.todolist.repositories.UserRepository;
import lt.ca.javau12.todolist.security.JwtUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtils = mock(JwtUtils.class);
        passwordEncoder = mock(PasswordEncoder.class);

        authService = new AuthService(userRepository, authenticationManager, jwtUtils, passwordEncoder);
    }

    @Test
    void createNewUser_savesUserIfUsernameIsNew() {
        String username = "testUser";
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        authService.createNewUser(username, rawPassword);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(username, savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
        assertTrue(savedUser.isEnabled());
        assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void createNewUser_throwsExceptionIfUsernameTaken() {
        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.createNewUser("existingUser", "password");
        });

        assertEquals("Username already taken.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void isNewUsername_returnsTrueWhenUserNotFound() {
        when(userRepository.findByUsername("newUser"))
                .thenReturn(Optional.empty());

        assertTrue(authService.isNewUsername("newUser"));
    }

    @Test
    void isNewUsername_returnsFalseWhenUserExists() {
        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(new User()));

        assertFalse(authService.isNewUsername("existingUser"));
    }

    @Test
    void getToken_returnsLoginResponseIfAuthenticationSucceeds() {
        String username = "test";
        String password = "pass";
        String token = "jwt-token";

        when(jwtUtils.generateToken(username)).thenReturn(token);

        LoginResponse response = authService.getToken(username, password);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        assertEquals(token, response.jwt());
    }
}
