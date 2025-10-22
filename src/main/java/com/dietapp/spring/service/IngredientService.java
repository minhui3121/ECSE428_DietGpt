package com.dietapp.spring.service;

import java.util.List;
import com.dietapp.spring.dto.IngredientDto;

public interface IngredientService {
    IngredientDto add(String name, String unit);
    List<IngredientDto> list();
    void delete(Long id);
}
