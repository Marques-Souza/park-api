package com.marquesdev.park_api.mapper;

import com.marquesdev.park_api.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.park_api.dto.UserRequestDto;
import com.marquesdev.park_api.dto.UserResponseDto;
import com.marquesdev.park_api.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;
    private final ResourceUrlBuilder resourceUrlBuilder;

    @Value("${app.api.endpoints.users}")
    private String userEndpoint;

    public User toUser(UserRequestDto userRequestDto){
        return modelMapper.map(userRequestDto, User.class);
    }

    public UserResponseDto toDto(User user){
        UserResponseDto response = modelMapper.map(user, UserResponseDto.class);
        response.setRole(
                user.getRole()
                        .name()
                        .substring("ROLE_".length())
        );
        response.setUrl(
                resourceUrlBuilder.build(userEndpoint, user.getId())
        );
        return response;
    }


}
