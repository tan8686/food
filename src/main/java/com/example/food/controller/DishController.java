    package com.example.food.controller;

    import com.example.food.dto.DishRequestDTO;
    import com.example.food.dto.DishResponseDTO;
    import com.example.food.dto.PageResponse;
    import com.example.food.entity.DishStatus;
    import com.example.food.service.DishService;
    import jakarta.validation.Valid;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/v1/dishes")
    public class DishController {

        @Autowired
        private DishService dishService;

        @GetMapping
        public ResponseEntity<PageResponse<DishResponseDTO>> getDishes(
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "5") int limit,
                @RequestParam(defaultValue = "startDate") String sortBy,
                @RequestParam(defaultValue = "desc") String sortDir,
                @RequestParam(defaultValue = "ON_SALE") String status,
                @RequestParam(required = false) String keyword,
                @RequestParam(required = false) Integer categoryId,
                @RequestParam(required = false) Double minPrice,
                @RequestParam(required = false) Double maxPrice
        ) {
            PageResponse<DishResponseDTO> response = dishService.getDishes(
                    page, limit, sortBy, sortDir, DishStatus.valueOf(status),
                    keyword, categoryId, minPrice, maxPrice
            );
            return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<DishResponseDTO> getDishById(@PathVariable String id) {
            DishResponseDTO dish = dishService.getDishById(id);
            return ResponseEntity.ok(dish);
        }

        @PostMapping
        public ResponseEntity<DishResponseDTO> createDish(@Valid @RequestBody DishRequestDTO request) {
            DishResponseDTO dish = dishService.createDish(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dish);
        }

        @PutMapping("/{id}")
        public ResponseEntity<DishResponseDTO> updateDish(
                @PathVariable String id,
                @Valid @RequestBody DishRequestDTO request
        ) {
            DishResponseDTO dish = dishService.updateDish(id, request);
            return ResponseEntity.ok(dish);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteDish(@PathVariable String id) {
            dishService.deleteDish(id);
            return ResponseEntity.noContent().build();
        }
    }