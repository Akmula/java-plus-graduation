package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.circuitbreaker.InternalUserFeignClientFallback;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.params.UserParamsAdmin;

import java.util.List;

@FeignClient(name = "user-service",
        contextId = "InternalUserFeignClient",
        path = "/admin/users",
        fallback = InternalUserFeignClientFallback.class)
public interface InternalUserFeignClient {

    @GetMapping("/{userId}")
    UserDto getByUserId(@PathVariable Long userId);

    @GetMapping
    List<UserDto> getAll(@SpringQueryMap UserParamsAdmin param);

}