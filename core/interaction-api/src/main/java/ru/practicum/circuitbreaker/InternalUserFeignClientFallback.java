package ru.practicum.circuitbreaker;

import org.springframework.stereotype.Component;
import ru.practicum.client.InternalUserFeignClient;
import ru.practicum.dto.UserDto;

@Component
public class InternalUserFeignClientFallback implements InternalUserFeignClient {

    @Override
    public UserDto getByUserId(Long userId) {
        return null;
    }
}