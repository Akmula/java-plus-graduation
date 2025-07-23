package ru.practicum.ewm.main.dto.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserParamsAdmin {
    private List<Long> ids;
    private int from = PaginationDefaults.DEFAULT_FROM;
    private int size = PaginationDefaults.DEFAULT_SIZE;
}