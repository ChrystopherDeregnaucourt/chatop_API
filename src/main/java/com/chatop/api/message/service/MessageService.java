package com.chatop.api.message.service;

import com.chatop.api.message.dto.MessageRequest;
import com.chatop.api.message.dto.MessageResponse;
import com.chatop.api.message.model.Message;
import com.chatop.api.message.repository.MessageRepository;
import com.chatop.api.rental.model.Rental;
import com.chatop.api.rental.service.RentalService;
import com.chatop.api.user.UserService;
import com.chatop.api.user.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final RentalService rentalService;

    public MessageService(MessageRepository messageRepository,
                          UserService userService,
                          RentalService rentalService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @Transactional
    public MessageResponse create(MessageRequest request, String authenticatedEmail) {
        User authenticatedUser = userService.getByEmail(authenticatedEmail);
        User requester = userService.getById(request.userId());
        if (!authenticatedUser.getId().equals(requester.getId())) {
            throw new AccessDeniedException("You cannot send messages on behalf of another user");
        }
        Rental rental = rentalService.getEntity(request.rentalId());
        Message message = Message.builder()
                .message(request.message())
                .user(authenticatedUser)
                .rental(rental)
                .build();
        Message saved = messageRepository.save(message);
        return new MessageResponse(saved.getId(), saved.getMessage(), rental.getId(), authenticatedUser.getId(), saved.getCreatedAt());
    }
}
