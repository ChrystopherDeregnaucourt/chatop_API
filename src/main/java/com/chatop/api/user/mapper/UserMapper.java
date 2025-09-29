package com.chatop.api.user.mapper;

import com.chatop.api.user.dto.UserResponse;
import com.chatop.api.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
