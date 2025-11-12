package com.dietapp.spring.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dietapp.spring.entity.MealEntity;
import java.util.List;
import java.util.Optional;

public interface MealHistoryRepository extends JpaRepository<MealEntity, Long> {
    List<MealEntity> findByUserId(Long userId);
    Optional<MealEntity> findByMealIdAndUserId(Long mealId, Long userId);
    boolean existsByMealIdAndUserId(Long mealId, Long userId);
}
