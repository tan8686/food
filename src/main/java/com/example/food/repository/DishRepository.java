package com.example.food.repository;

import com.example.food.entity.Dish;
import com.example.food.entity.DishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DishRepository extends JpaRepository<Dish, String> {

    @Query("""
        SELECT d FROM Dish d
        WHERE (:status IS NULL OR d.status = :status)
          AND (:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR d.category.id = :categoryId)
          AND (:minPrice IS NULL OR d.price >= :minPrice)
          AND (:maxPrice IS NULL OR d.price <= :maxPrice)
    """)
    Page<Dish> findDishesWithFilters(
            @Param("status") DishStatus status,
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}
