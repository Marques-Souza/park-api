package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.PasswordRequestDto;
import com.marquesdev.park_api.dto.UserRequestDto;
import com.marquesdev.park_api.dto.UserResponseDto;
import com.marquesdev.park_api.entity.User;
import com.marquesdev.park_api.enums.Role;
import com.marquesdev.park_api.exception.EntityNotFoundException;
import com.marquesdev.park_api.exception.PasswordInvalidException;
import com.marquesdev.park_api.exception.UsernameUniqueException;
import com.marquesdev.park_api.mapper.UserMapper;
import com.marquesdev.park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto saveUser( UserRequestDto userRequestDto) {
        User user = userMapper.toUser(userRequestDto);
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.saveAndFlush(user);
            return userMapper.toDto(savedUser);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UsernameUniqueException("Username already exists.");
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponseDto updatePassword(UUID id, PasswordRequestDto passwordRequestDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        if (!passwordRequestDto.getNewPassword().equals(passwordRequestDto.getConfirmPassword())) {
            throw new PasswordInvalidException("New password and confirmation password do not match.");
        }

        if (!passwordEncoder.matches(passwordRequestDto.getCurrentPassword(), existingUser.getPassword())) {
            throw new PasswordInvalidException("Current password is incorrect.");
        }

        existingUser.setPassword(passwordEncoder.encode(passwordRequestDto.getNewPassword()));
        return userMapper.toDto(existingUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public Role findRoleByUsername(String username) {
        return userRepository.findRoleByUsername(username);

    }
}
