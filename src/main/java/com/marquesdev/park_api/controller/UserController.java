package com.marquesdev.park_api.controller;

import com.marquesdev.park_api.dto.PasswordRequestDto;
import com.marquesdev.park_api.dto.UserRequestDto;
import com.marquesdev.park_api.dto.UserResponseDto;
import com.marquesdev.park_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${app.api.endpoints.users}")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto){
       UserResponseDto user = userService.saveUser(userRequestDto);
       return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID id){
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<UserResponseDto> updatePassword(@PathVariable UUID id,@Valid @RequestBody PasswordRequestDto passwordRequestDto){
        UserResponseDto updatedUser = userService.updatePassword(id,passwordRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        List<UserResponseDto> users = userService.getAll();
        return ResponseEntity.ok(users);
    }
}
