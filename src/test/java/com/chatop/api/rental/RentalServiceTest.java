package com.chatop.api.rental;

import com.chatop.api.common.exception.BadRequestException;
import com.chatop.api.rental.dto.RentalRequest;
import com.chatop.api.rental.mapper.RentalMapper;
import com.chatop.api.rental.model.Rental;
import com.chatop.api.rental.repository.RentalRepository;
import com.chatop.api.rental.service.RentalService;
import com.chatop.api.storage.FileStorageService;
import com.chatop.api.user.UserService;
import com.chatop.api.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileStorageService fileStorageService;

    private RentalMapper rentalMapper;

    private RentalService rentalService;

    @Captor
    private ArgumentCaptor<Rental> rentalCaptor;

    @BeforeEach
    void setUp() {
        rentalMapper = new RentalMapper(fileStorageService);
        rentalService = new RentalService(rentalRepository, userService, fileStorageService, rentalMapper);
    }

    @Test
    void create_shouldPersistRentalAndStorePicture() {
        RentalRequest request = new RentalRequest();
        request.setName("Cozy loft");
        request.setSurface(45);
        request.setPrice(1200);
        request.setDescription("Nice place");
        request.setPicture(new MockMultipartFile("picture", "image.jpg", "image/jpeg", "data".getBytes()));

        User owner = User.builder().id(5L).name("Owner").email("owner@example.com").build();
        given(userService.getByEmail("owner@example.com")).willReturn(owner);
        given(fileStorageService.store(any())).willReturn("stored.jpg");
        given(fileStorageService.buildPublicUrl("stored.jpg")).willReturn("/files/stored.jpg");
        given(rentalRepository.save(any(Rental.class))).willAnswer(invocation -> invocation.getArgument(0));

        var response = rentalService.create(request, "owner@example.com");

        verify(rentalRepository).save(rentalCaptor.capture());
        Rental saved = rentalCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Cozy loft");
        assertThat(saved.getPicturePath()).isEqualTo("stored.jpg");
        assertThat(response.pictureUrl()).isEqualTo("/files/stored.jpg");
    }

    @Test
    void create_withoutPicture_shouldThrowBadRequest() {
        RentalRequest request = new RentalRequest();
        request.setName("Cozy loft");
        request.setSurface(45);
        request.setPrice(1200);

        assertThatThrownBy(() -> rentalService.create(request, "owner@example.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Picture is required");
    }
}
