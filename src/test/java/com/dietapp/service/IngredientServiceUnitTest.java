package com.dietapp.service;

import com.dietapp.spring.dto.IngredientDto;
import com.dietapp.spring.entity.IngredientEntity;
import com.dietapp.spring.repo.IngredientRepository;
import com.dietapp.spring.service.IngredientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class IngredientServiceUnitTest {

    private IngredientRepository repo;
    private IngredientServiceImpl service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(IngredientRepository.class);
        service = new IngredientServiceImpl(repo);
    }

    @Test
    void add_success() {
        when(repo.existsByNameIgnoreCase("Rice")).thenReturn(false);
        when(repo.save(any())).thenAnswer(inv -> {
            IngredientEntity e = inv.getArgument(0);
            // simulate JPA generated id
            try {
                java.lang.reflect.Field idf = IngredientEntity.class.getDeclaredField("id");
                idf.setAccessible(true);
                idf.set(e, 1L);
            } catch (Exception ignore) {}
            return e;
        });
        IngredientDto dto = service.add(" Rice ", "gram");
        assertEquals("Rice", dto.name());
        assertEquals("GRAM", dto.unit());
        assertNotNull(dto.id());
    }

    @Test
    void add_duplicate_rejected() {
        when(repo.existsByNameIgnoreCase("Rice")).thenReturn(true);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.add("Rice", "GRAM"));
        assertTrue(ex.getMessage().toLowerCase().contains("exists"));
    }

    @Test
    void add_invalid_unit_rejected() {
        when(repo.existsByNameIgnoreCase("Milk")).thenReturn(false);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.add("Milk", "bottle"));
        assertTrue(ex.getMessage().toLowerCase().contains("invalid unit"));
    }

    @Test
    void list_returns_items() {
        IngredientEntity e = new IngredientEntity("Egg", IngredientEntity.Unit.PIECE);
        try { // set id
            java.lang.reflect.Field idf = IngredientEntity.class.getDeclaredField("id");
            idf.setAccessible(true);
            idf.set(e, 2L);
        } catch (Exception ignore) {}
        when(repo.findAll()).thenReturn(List.of(e));
        List<IngredientDto> list = service.list();
        assertEquals(1, list.size());
        assertEquals("Egg", list.get(0).name());
        assertEquals("PIECE", list.get(0).unit());
    }
}
