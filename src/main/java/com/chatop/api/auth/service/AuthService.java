package com.chatop.api.auth.service;

import com.chatop.api.auth.dto.*;
import com.chatop.api.security.jwt.JwtTokenService;
import com.chatop.api.user.UserService;
import com.chatop.api.user.dto.UserResponse;
import com.chatop.api.user.mapper.UserMapper;
import com.chatop.api.user.model.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenService jwtTokenService,
                       UserService userService,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public RegisterResponse register(RegisterRequest request) {
        User user = userService.registerUser(request.name(), request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenService.generateToken(userDetails);
        return new RegisterResponse(userMapper.toResponse(user), AuthResponse.bearer(token, jwtTokenService.getExpirationSeconds()));
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login().toLowerCase(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenService.generateToken(userDetails);
        return AuthResponse.bearer(token, jwtTokenService.getExpirationSeconds());
    }

    public UserResponse me(String email) {
        User user = userService.getByEmail(email);
        return userMapper.toResponse(user);
    }
}
