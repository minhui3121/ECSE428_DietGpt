package com.dietapp.spring.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "ingredients",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})}
)
public class IngredientEntity {

    public enum Unit {
        GRAM, KILOGRAM, MILLILITER, LITER, PIECE, TEASPOON, TABLESPOON, CUP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Unit unit;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public IngredientEntity() {}

    public IngredientEntity(String name, Unit unit) {
        this.name = name;
        this.unit = unit;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }
    public Instant getCreatedAt() { return createdAt; }
}
