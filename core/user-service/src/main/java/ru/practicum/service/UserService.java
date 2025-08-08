package ru.practicum.service;

import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.params.UserParamsAdmin;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest request);

    UserDto getUser(Long userId);

    List<UserDto> getUsers(UserParamsAdmin param);

    void deleteUser(Long userId);

}