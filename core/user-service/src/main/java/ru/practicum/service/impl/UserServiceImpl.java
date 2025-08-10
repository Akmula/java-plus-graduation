package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.params.UserParamsAdmin;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User with this email already exists");
        }
        User user = UserMapper.toEntity(request);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user with id: " + userId + " not found!"));
        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsers(UserParamsAdmin param) {
        List<User> users = (param.getIds() != null && !param.getIds().isEmpty())
                ? userRepository.findAllByIdIn(param.getIds())
                : userRepository.findAll(PageRequest.of(param.getFrom() / param.getSize(),
                param.getSize())).getContent();

        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user with id: " + userId + " not found!"));
        userRepository.deleteById(userId);
    }
}