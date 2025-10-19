package com.dietapp.spring.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dietapp.spring.entity.FoodEntity;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<FoodEntity, String> {
    Optional<FoodEntity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
