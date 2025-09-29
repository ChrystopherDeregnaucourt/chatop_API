package com.chatop.api.auth;

import com.chatop.api.common.exception.BadRequestException;
import com.chatop.api.user.UserService;
import com.chatop.api.user.model.User;
import com.chatop.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldEncodePasswordAndNormalizeEmail() {
        given(userRepository.existsByEmail("user@example.com")).willReturn(false);
        given(passwordEncoder.encode("Password123!")).willReturn("hashed");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User user = userService.registerUser("Alice", "User@Example.com", "Password123!");

        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hashed");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123!");
    }

    @Test
    void registerUser_whenEmailExists_shouldThrowBadRequest() {
        given(userRepository.existsByEmail("user@example.com")).willReturn(true);

        assertThatThrownBy(() -> userService.registerUser("Alice", "user@example.com", "Password123!"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email is already registered");
    }
}
