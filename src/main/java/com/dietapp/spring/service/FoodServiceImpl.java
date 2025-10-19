package com.dietapp.spring.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dietapp.spring.repo.FoodRepository;
import com.dietapp.spring.entity.FoodEntity;
import com.dietapp.spring.dto.FoodDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl {
    private final FoodRepository repo;

    public FoodServiceImpl(FoodRepository repo) { this.repo = repo; }

    @Transactional
    public FoodDto add(FoodDto dto) {
        if (repo.existsByNameIgnoreCase(dto.getName())) throw new IllegalArgumentException("Food with this name already exists");
        FoodEntity e = new FoodEntity();
        e.setName(dto.getName()); e.setCalories(dto.getCalories()); e.setServingSize(dto.getServingSize()); e.setQuantity(dto.getQuantity()); e.setUnit(dto.getUnit());
        e = repo.save(e);
        dto.setId(e.getId());
        return dto;
    }

    public List<FoodDto> list() {
        return repo.findAll().stream().map(e -> {
            FoodDto d = new FoodDto();
            d.setId(e.getId()); d.setName(e.getName()); d.setCalories(e.getCalories()); d.setServingSize(e.getServingSize()); d.setQuantity(e.getQuantity()); d.setUnit(e.getUnit());
            return d;
        }).collect(Collectors.toList());
    }

    @Transactional
    public FoodDto update(String id, FoodDto dto) {
        FoodEntity e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Food not found"));
        if (repo.existsByNameIgnoreCase(dto.getName()) && !e.getName().equalsIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Food with this name already exists");
        }
        e.setName(dto.getName()); e.setCalories(dto.getCalories()); e.setServingSize(dto.getServingSize()); e.setQuantity(dto.getQuantity()); e.setUnit(dto.getUnit());
        repo.save(e);
        dto.setId(e.getId());
        return dto;
    }

    @Transactional
    public void delete(String id) { repo.deleteById(id); }
}
