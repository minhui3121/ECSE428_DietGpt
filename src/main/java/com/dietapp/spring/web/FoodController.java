package com.dietapp.spring.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dietapp.spring.dto.FoodDto;
import com.dietapp.spring.service.FoodServiceImpl;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {
    private final FoodServiceImpl svc;
    public FoodController(FoodServiceImpl svc) { this.svc = svc; }

    @PostMapping
    public ResponseEntity<FoodDto> create(@RequestBody FoodDto dto) {
        FoodDto created = svc.add(dto);
        return ResponseEntity.created(URI.create("/api/foods/" + created.getId())).body(created);
    }

    @GetMapping
    public List<FoodDto> list() { return svc.list(); }

    @PutMapping("/{id}")
    public FoodDto update(@PathVariable String id, @RequestBody FoodDto dto) { return svc.update(id, dto); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) { svc.delete(id); return ResponseEntity.noContent().build(); }
}
