package com.marquesdev.park_api.service;

import com.marquesdev.park_api.dto.PasswordRequestDto;
import com.marquesdev.park_api.dto.UserRequestDto;
import com.marquesdev.park_api.dto.UserResponseDto;
import com.marquesdev.park_api.entity.User;
import com.marquesdev.park_api.mapper.UserMapper;
import com.marquesdev.park_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto saveUser(UserRequestDto userRequestDto) {
        User user = userMapper.toUser(userRequestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponseDto updatePassword(UUID id, PasswordRequestDto passwordRequestDto) {
        User existingUser = userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (!passwordRequestDto.getNewPassword().equals(passwordRequestDto.getConfirmPassword())){
            throw new RuntimeException("New password and confirmation password do not match.");
        }

        if (!existingUser.getPassword().equals(passwordRequestDto.getCurrentPassword())){
            throw new RuntimeException("Current password is incorrect.");
        }

        existingUser.setPassword(passwordRequestDto.getNewPassword());
        return userMapper.toDto(existingUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
