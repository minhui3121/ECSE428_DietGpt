package com.dietapp.spring.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;
import java.util.Map;

import com.dietapp.spring.dto.MealHistoryDto;
import com.dietapp.spring.service.MealHistoryServiceImpl;

@RestController
@RequestMapping("/api/meal-history")
public class MealHistoryController {

    private final MealHistoryServiceImpl svc;

    public MealHistoryController(MealHistoryServiceImpl svc) {
        this.svc = svc;
    }

    @GetMapping("/{userId}")
    public List<MealHistoryDto> list(@PathVariable Long userId) {
        return svc.list(userId);
    }

    @GetMapping("/{userId}/{mealId}")
    public ResponseEntity<?> getMeal(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            MealHistoryDto meal = svc.get(userId, mealId);
            return ResponseEntity.ok(meal);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> create(@PathVariable Long userId, @RequestBody MealHistoryDto dto) {
        try {
            MealHistoryDto created = svc.add(dto, userId);
            return ResponseEntity
                    .created(URI.create("/api/meal-history/" + userId + "/" + created.getMealId()))
                    .body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{userId}/{mealId}")
    public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            svc.delete(userId, mealId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
