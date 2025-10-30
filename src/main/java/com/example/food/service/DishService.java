package com.example.food.service;

import com.example.food.dto.DishRequestDTO;
import com.example.food.dto.DishResponseDTO;
import com.example.food.dto.PageResponse;
import com.example.food.entity.Category;
import com.example.food.entity.Dish;
import com.example.food.entity.DishStatus;
import com.example.food.exception.BadRequestException;
import com.example.food.exception.ConflictException;
import com.example.food.exception.ResourceNotFoundException;
import com.example.food.repository.CategoryRepository;
import com.example.food.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public PageResponse<DishResponseDTO> getDishes(
            int page, int limit, String sortBy, String sortDir,
            DishStatus status, String keyword, Integer categoryId,
            Double minPrice, Double maxPrice
    ) {
        if (page < 1) {
            throw new BadRequestException("Page phải lớn hơn hoặc bằng 1");
        }

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Dish> dishPage = dishRepository.findDishesWithFilters(
                status, keyword, categoryId, minPrice, maxPrice, pageable
        );

        List<DishResponseDTO> content = dishPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageResponse<DishResponseDTO> response = new PageResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setLimit(limit);
        response.setTotalPages(dishPage.getTotalPages());
        response.setTotalElements(dishPage.getTotalElements());

        return response;
    }

    public DishResponseDTO getDishById(String id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));
        return convertToDTO(dish);
    }

    public DishResponseDTO createDish(DishRequestDTO request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy danh mục với id: " + request.getCategoryId()));

        Dish dish = new Dish();
        dish.setId("MN" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setImageUrl(request.getImageUrl());
        dish.setPrice(request.getPrice());
        dish.setCategory(category);
        dish.setStatus(DishStatus.ON_SALE);
        dish.setStartDate(LocalDateTime.now());
        dish.setLastModifiedDate(LocalDateTime.now());

        Dish savedDish = dishRepository.save(dish);
        return convertToDTO(savedDish);
    }

    public DishResponseDTO updateDish(String id, DishRequestDTO request) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));

        if (dish.getStatus() == DishStatus.DELETED) {
            throw new BadRequestException("Không thể sửa món ăn đã bị xóa");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy danh mục với id: " + request.getCategoryId()));

        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setImageUrl(request.getImageUrl());
        dish.setPrice(request.getPrice());
        dish.setCategory(category);

        if (request.getStatus() != null) {
            dish.setStatus(request.getStatus());
        }

        dish.setLastModifiedDate(LocalDateTime.now());

        Dish updatedDish = dishRepository.save(dish);
        return convertToDTO(updatedDish);
    }

    public void deleteDish(String id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn với id: " + id));

        if (dish.getStatus() == DishStatus.DELETED) {
            throw new ConflictException("Món ăn đã bị xóa trước đó");
        }

        dish.setStatus(DishStatus.DELETED);
        dish.setLastModifiedDate(LocalDateTime.now());
        dishRepository.save(dish);
    }

    private DishResponseDTO convertToDTO(Dish dish) {
        DishResponseDTO dto = new DishResponseDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setDescription(dish.getDescription());
        dto.setImageUrl(dish.getImageUrl());
        dto.setPrice(dish.getPrice());
        dto.setStartDate(dish.getStartDate());
        dto.setLastModifiedDate(dish.getLastModifiedDate());
        dto.setStatus(dish.getStatus());
        dto.setCategoryId(dish.getCategory().getId());
        dto.setCategoryName(dish.getCategory().getName());
        return dto;
    }
}