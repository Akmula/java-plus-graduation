package ru.practicum.service;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    CategoryDto updateCategory(Long catId, CategoryDto dto);

    void deleteCategory(Long catId);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long catId);
}