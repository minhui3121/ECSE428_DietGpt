package com.dietapp.spring.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dietapp.spring.dto.IngredientDto;
import com.dietapp.spring.entity.IngredientEntity;
import com.dietapp.spring.entity.IngredientEntity.Unit;
import com.dietapp.spring.repo.IngredientRepository;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository repo;

    public IngredientServiceImpl(IngredientRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public IngredientDto add(String name, String unit) {
        String cleaned = normalize(name);
        if (cleaned.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (repo.existsByNameIgnoreCase(cleaned)) {
            throw new IllegalArgumentException("Ingredient already exists");
        }
        Unit u;
        try {
            u = Unit.valueOf(unit.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid unit. Allowed: " + java.util.Arrays.toString(Unit.values()));
        }
        IngredientEntity saved = repo.save(new IngredientEntity(cleaned, u));
        return new IngredientDto(saved.getId(), saved.getName(), saved.getUnit().name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientDto> list() {
        return repo.findAll().stream()
                .map(i -> new IngredientDto(i.getId(), i.getName(), i.getUnit().name()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().replaceAll("\s+", " ");
    }
}
