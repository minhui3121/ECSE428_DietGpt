package com.dietapp.spring.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dietapp.spring.entity.IngredientEntity;

public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<IngredientEntity> findByNameIgnoreCase(String name);
}
