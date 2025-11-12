package com.dietapp.spring.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dietapp.spring.dto.MealHistoryDto;
import com.dietapp.spring.entity.MealEntity;
import com.dietapp.spring.repo.MealHistoryRepository;

@Service
public class MealHistoryServiceImpl {

    private final MealHistoryRepository repo;

    public MealHistoryServiceImpl(MealHistoryRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public MealHistoryDto add(MealHistoryDto dto, Long userId) {
        MealEntity entity = new MealEntity();
        entity.setUserId(userId);
        entity.setDate(dto.getDate());
        entity.setMealName(dto.getMealName());
        entity.setIngredients(dto.getIngredients());
        entity.setCalories(dto.getCalories());
        entity.setTags(dto.getTags());
        entity = repo.save(entity);
        dto.setMealId(entity.getMealId());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<MealHistoryDto> list(Long userId) {
        return repo.findByUserId(userId)
                .stream()
                .map(e -> new MealHistoryDto(
                        e.getMealId(),
                        e.getDate(),
                        e.getMealName(),
                        e.getIngredients(),
                        e.getCalories(),
                        e.getTags()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MealHistoryDto get(Long userId, Long mealId) {
        MealEntity e = repo.findByMealIdAndUserId(mealId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        return new MealHistoryDto(
                e.getMealId(),
                e.getDate(),
                e.getMealName(),
                e.getIngredients(),
                e.getCalories(),
                e.getTags());
    }

    @Transactional
    public void delete(Long userId, Long mealId) {
        if (!repo.existsByMealIdAndUserId(mealId, userId)) {
            throw new IllegalArgumentException("Meal not found for this user");
        }
        repo.deleteById(mealId);
    }
}
