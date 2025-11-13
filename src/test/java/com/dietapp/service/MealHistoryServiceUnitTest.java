package com.dietapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.dietapp.spring.dto.MealHistoryDto;
import com.dietapp.spring.entity.MealEntity;
import com.dietapp.spring.repo.MealHistoryRepository;
import com.dietapp.spring.service.MealHistoryServiceImpl;

public class MealHistoryServiceUnitTest {

    private MealHistoryRepository repo;
    private MealHistoryServiceImpl service;

    @BeforeEach
    public void setup() {
        repo = Mockito.mock(MealHistoryRepository.class);
        service = new MealHistoryServiceImpl(repo);
    }

    @Test
    public void testAddMeal_Success() {
        // Arrange
        Long userId = 1L;
        MealHistoryDto dto = new MealHistoryDto();
        dto.setDate(LocalDate.of(2025, 11, 12));
        dto.setMealName("Grilled Chicken Salad");
        dto.setIngredients("chicken, lettuce, tomatoes");
        dto.setCalories(450.0);
        dto.setTags("healthy, protein");

        when(repo.save(any(MealEntity.class))).thenAnswer(invocation -> {
            MealEntity saved = invocation.getArgument(0);
            // Simulate database auto-generating ID
            try {
                java.lang.reflect.Field idField = MealEntity.class.getDeclaredField("mealId");
                idField.setAccessible(true);
                idField.set(saved, 100L);
                idField.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return saved;
        });

        // Act
        MealHistoryDto result = service.add(dto, userId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getMealId());
        assertEquals("Grilled Chicken Salad", result.getMealName());
        assertEquals(450.0, result.getCalories());
        verify(repo, times(1)).save(any(MealEntity.class));
    }

    @Test
    public void testListMeals_ReturnsUserMeals() {
        // Arrange
        Long userId = 1L;
        
        MealEntity meal1 = new MealEntity();
        meal1.setMealId(1L);
        meal1.setUserId(userId);
        meal1.setDate(LocalDate.of(2025, 11, 10));
        meal1.setMealName("Breakfast");
        meal1.setIngredients("eggs, toast");
        meal1.setCalories(300.0);
        meal1.setTags("breakfast");

        MealEntity meal2 = new MealEntity();
        meal2.setMealId(2L);
        meal2.setUserId(userId);
        meal2.setDate(LocalDate.of(2025, 11, 11));
        meal2.setMealName("Lunch");
        meal2.setIngredients("pasta, sauce");
        meal2.setCalories(600.0);
        meal2.setTags("lunch");

        when(repo.findByUserId(userId)).thenReturn(Arrays.asList(meal1, meal2));

        // Act
        List<MealHistoryDto> results = service.list(userId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Breakfast", results.get(0).getMealName());
        assertEquals("Lunch", results.get(1).getMealName());
        assertEquals(300.0, results.get(0).getCalories());
        assertEquals(600.0, results.get(1).getCalories());
        verify(repo, times(1)).findByUserId(userId);
    }

    @Test
    public void testListMeals_EmptyList() {
        // Arrange
        Long userId = 99L;
        when(repo.findByUserId(userId)).thenReturn(Arrays.asList());

        // Act
        List<MealHistoryDto> results = service.list(userId);

        // Assert
        assertNotNull(results);
        assertEquals(0, results.size());
        verify(repo, times(1)).findByUserId(userId);
    }

    @Test
    public void testGetMeal_Success() {
        // Arrange
        Long userId = 1L;
        Long mealId = 10L;

        MealEntity meal = new MealEntity();
        meal.setMealId(mealId);
        meal.setUserId(userId);
        meal.setDate(LocalDate.of(2025, 11, 12));
        meal.setMealName("Dinner");
        meal.setIngredients("steak, potatoes");
        meal.setCalories(800.0);
        meal.setTags("dinner, protein");

        when(repo.findByMealIdAndUserId(mealId, userId)).thenReturn(Optional.of(meal));

        // Act
        MealHistoryDto result = service.get(userId, mealId);

        // Assert
        assertNotNull(result);
        assertEquals(mealId, result.getMealId());
        assertEquals("Dinner", result.getMealName());
        assertEquals(800.0, result.getCalories());
        verify(repo, times(1)).findByMealIdAndUserId(mealId, userId);
    }

    @Test
    public void testGetMeal_NotFound() {
        // Arrange
        Long userId = 1L;
        Long mealId = 999L;

        when(repo.findByMealIdAndUserId(mealId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.get(userId, mealId);
        });

        assertEquals("Meal not found", exception.getMessage());
        verify(repo, times(1)).findByMealIdAndUserId(mealId, userId);
    }

    @Test
    public void testGetMeal_WrongUser() {
        // Arrange
        Long userId = 1L;
        Long wrongUserId = 2L;
        Long mealId = 10L;

        when(repo.findByMealIdAndUserId(mealId, wrongUserId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.get(wrongUserId, mealId);
        });

        assertEquals("Meal not found", exception.getMessage());
        verify(repo, times(1)).findByMealIdAndUserId(mealId, wrongUserId);
    }

    @Test
    public void testDeleteMeal_Success() {
        // Arrange
        Long userId = 1L;
        Long mealId = 10L;

        when(repo.existsByMealIdAndUserId(mealId, userId)).thenReturn(true);
        doNothing().when(repo).deleteById(mealId);

        // Act
        service.delete(userId, mealId);

        // Assert
        verify(repo, times(1)).existsByMealIdAndUserId(mealId, userId);
        verify(repo, times(1)).deleteById(mealId);
    }

    @Test
    public void testDeleteMeal_NotFound() {
        // Arrange
        Long userId = 1L;
        Long mealId = 999L;

        when(repo.existsByMealIdAndUserId(mealId, userId)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.delete(userId, mealId);
        });

        assertEquals("Meal not found for this user", exception.getMessage());
        verify(repo, times(1)).existsByMealIdAndUserId(mealId, userId);
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    public void testDeleteMeal_WrongUser() {
        // Arrange
        Long userId = 1L;
        Long wrongUserId = 2L;
        Long mealId = 10L;

        when(repo.existsByMealIdAndUserId(mealId, wrongUserId)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.delete(wrongUserId, mealId);
        });

        assertEquals("Meal not found for this user", exception.getMessage());
        verify(repo, times(1)).existsByMealIdAndUserId(mealId, wrongUserId);
        verify(repo, never()).deleteById(anyLong());
    }

    @Test
    public void testAddMeal_WithAllFields() {
        // Arrange
        Long userId = 5L;
        MealHistoryDto dto = new MealHistoryDto();
        dto.setDate(LocalDate.of(2025, 11, 12));
        dto.setMealName("Protein Smoothie");
        dto.setIngredients("banana, protein powder, milk");
        dto.setCalories(250.0);
        dto.setTags("snack, protein, vegetarian");

        when(repo.save(any(MealEntity.class))).thenAnswer(invocation -> {
            MealEntity saved = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = MealEntity.class.getDeclaredField("mealId");
                idField.setAccessible(true);
                idField.set(saved, 50L);
                idField.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return saved;
        });

        // Act
        MealHistoryDto result = service.add(dto, userId);

        // Assert
        assertNotNull(result);
        assertEquals(50L, result.getMealId());
        assertEquals("Protein Smoothie", result.getMealName());
        assertEquals("banana, protein powder, milk", result.getIngredients());
        assertEquals(250.0, result.getCalories());
        assertEquals("snack, protein, vegetarian", result.getTags());
        verify(repo, times(1)).save(argThat(entity -> 
            entity.getUserId().equals(userId) &&
            entity.getMealName().equals("Protein Smoothie") &&
            entity.getCalories().equals(250.0)
        ));
    }

    @Test
    public void testListMeals_MultipleUsers() {
        // Arrange
        Long user1Id = 1L;
        Long user2Id = 2L;

        MealEntity meal1 = new MealEntity();
        meal1.setMealId(1L);
        meal1.setUserId(user1Id);
        meal1.setMealName("User1 Meal");
        meal1.setCalories(400.0);

        MealEntity meal2 = new MealEntity();
        meal2.setMealId(2L);
        meal2.setUserId(user2Id);
        meal2.setMealName("User2 Meal");
        meal2.setCalories(500.0);

        when(repo.findByUserId(user1Id)).thenReturn(Arrays.asList(meal1));
        when(repo.findByUserId(user2Id)).thenReturn(Arrays.asList(meal2));

        // Act
        List<MealHistoryDto> user1Results = service.list(user1Id);
        List<MealHistoryDto> user2Results = service.list(user2Id);

        // Assert
        assertEquals(1, user1Results.size());
        assertEquals(1, user2Results.size());
        assertEquals("User1 Meal", user1Results.get(0).getMealName());
        assertEquals("User2 Meal", user2Results.get(0).getMealName());
    }
}
