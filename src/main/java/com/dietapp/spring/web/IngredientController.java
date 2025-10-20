package com.dietapp.spring.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.net.URI;
import java.util.List;
import com.dietapp.spring.dto.IngredientDto;
import com.dietapp.spring.service.IngredientService;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService service;

    public IngredientController(IngredientService service) {
        this.service = service;
    }

    @GetMapping
    public List<IngredientDto> list() {
        return service.list();
    }

    public record CreateReq(String name, String unit) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateReq req) {
        try {
            IngredientDto dto = service.add(req.name(), req.unit());
            return ResponseEntity.created(URI.create("/api/ingredients/" + dto.id())).body(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
