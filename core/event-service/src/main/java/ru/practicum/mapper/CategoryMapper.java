package ru.practicum.mapper;

import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toCategory(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}