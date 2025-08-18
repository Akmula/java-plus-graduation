package ru.practicum.circuitbreaker;

import org.springframework.stereotype.Component;
import ru.practicum.client.InternalUserFeignClient;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.params.UserParamsAdmin;

import java.util.List;

@Component
public class InternalUserFeignClientFallback implements InternalUserFeignClient {

    @Override
    public UserDto getByUserId(Long userId) {
        return null;
    }

    @Override
    public List<UserDto> getAll(UserParamsAdmin param) {
        return List.of();
    }
}