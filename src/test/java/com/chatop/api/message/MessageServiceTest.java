package com.chatop.api.message;

import com.chatop.api.message.dto.MessageRequest;
import com.chatop.api.message.repository.MessageRepository;
import com.chatop.api.message.service.MessageService;
import com.chatop.api.rental.model.Rental;
import com.chatop.api.rental.service.RentalService;
import com.chatop.api.user.UserService;
import com.chatop.api.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserService userService;

    @Mock
    private RentalService rentalService;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, userService, rentalService);
    }

    @Test
    void create_whenUserIdDoesNotMatchAuthenticatedUser_shouldThrow() {
        MessageRequest request = new MessageRequest("Hello", 2L, 10L);
        User authenticated = User.builder().id(1L).email("auth@example.com").build();
        User provided = User.builder().id(2L).email("other@example.com").build();
        given(userService.getByEmail("auth@example.com")).willReturn(authenticated);
        given(userService.getById(2L)).willReturn(provided);

        assertThatThrownBy(() -> messageService.create(request, "auth@example.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("cannot send messages on behalf of another user");
    }
}
