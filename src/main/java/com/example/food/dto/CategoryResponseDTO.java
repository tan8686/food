package com.example.food.dto;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Integer id;
    private String name;

    public CategoryResponseDTO(Integer id, String name) {
    }
}