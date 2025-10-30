package com.example.food.service;

import com.example.food.dto.CategoryResponseDTO;
import com.example.food.dto.CategoryResponseDTO;
import com.example.food.entity.Category;
import com.example.food.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CategoryResponseDTO convertToDTO(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
