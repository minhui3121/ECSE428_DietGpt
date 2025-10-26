package com.dietapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dietapp.model.Ingredient;

public class IngredientServiceUnitTest {

    private static final Path STORE = Paths.get("data/ingredients.json");

    @BeforeEach
    public void cleanup() {
        try { Files.deleteIfExists(STORE); } catch (Exception ignore) {}
    }

    @Test
    public void addValidIngredient_minimal() {
        IngredientService svc = new IngredientService();
        Ingredient ing = new Ingredient();
        ing.setName("Salt");
        ing.setUnit("g"); // optional
        IngredientService.ValidationResult vr = svc.addIngredient(ing);
        assertTrue(vr.success, "valid ingredient with just name (and optional unit) should be accepted");
        List<Ingredient> all = svc.getIngredients();
        assertEquals(1, all.size());
        assertEquals("Salt", all.get(0).getName());
        // normalized to uppercase if provided
        assertEquals("G", all.get(0).getUnit());
    }

    @Test
    public void allowZeroCaloriesAndQuantity() {
        IngredientService svc = new IngredientService();
        Ingredient ing = new Ingredient();
        ing.setName("Water");
        ing.setUnit("ml");
        ing.setCaloriesPerUnit(0.0);
        ing.setQuantity(0.0);
        IngredientService.ValidationResult vr = svc.addIngredient(ing);
        assertTrue(vr.success, "zero calories & zero quantity should be allowed");
        assertEquals("ML", svc.getIngredients().get(0).getUnit()); // normalized
    }

    @Test
    public void rejectNegativeCalories() {
        IngredientService svc = new IngredientService();
        Ingredient ing = new Ingredient();
        ing.setName("Butter");
        ing.setUnit("g");
        ing.setCaloriesPerUnit(-1.0);
        IngredientService.ValidationResult vr = svc.addIngredient(ing);
        assertFalse(vr.success, "negative calories should be rejected");
    }

    @Test
    public void rejectNegativeQuantity() {
        IngredientService svc = new IngredientService();
        Ingredient ing = new Ingredient();
        ing.setName("Flour");
        ing.setUnit("g");
        ing.setQuantity(-5.0);
        IngredientService.ValidationResult vr = svc.addIngredient(ing);
        assertFalse(vr.success, "negative quantity should be rejected");
    }

    @Test
    public void rejectEmptyName() {
        IngredientService svc = new IngredientService();
        Ingredient ing = new Ingredient();
        ing.setName("  "); // empty after trim
        IngredientService.ValidationResult vr = svc.addIngredient(ing);
        assertFalse(vr.success, "empty name should be rejected");
    }

    @Test
    public void rejectDuplicateName_caseInsensitive() {
        IngredientService svc = new IngredientService();

        Ingredient a = new Ingredient();
        a.setName("Sugar");
        a.setUnit("g");
        assertTrue(svc.addIngredient(a).success);

        Ingredient b = new Ingredient();
        b.setName("sugar"); // same name, different case
        b.setUnit("g");
        IngredientService.ValidationResult vr = svc.addIngredient(b);
        assertFalse(vr.success, "duplicate name (case-insensitive) should be rejected");
    }

    @Test
    public void updateIngredient_success() {
        IngredientService svc = new IngredientService();

        Ingredient a = new Ingredient();
        a.setName("Oil");
        a.setUnit("ml");
        assertTrue(svc.addIngredient(a).success);

        Ingredient stored = svc.getIngredients().get(0);

        Ingredient updated = new Ingredient();
        updated.setName("Olive Oil");
        updated.setUnit("ml");               // will normalize to "ML"
        updated.setCaloriesPerUnit(884.0);
        updated.setQuantity(100.0);

        IngredientService.ValidationResult vr = svc.updateIngredient(stored.getId(), updated);
        assertTrue(vr.success, "update should succeed");

        Ingredient after = svc.getIngredients().get(0);
        assertEquals("Olive Oil", after.getName());
        assertEquals("ML", after.getUnit()); // <-- expect normalized uppercase
        assertEquals(884.0, after.getCaloriesPerUnit(), 1e-9);
        assertEquals(100.0, after.getQuantity(), 1e-9);
    }

    @Test
    public void updateRejectsDuplicateName() {
        IngredientService svc = new IngredientService();

        Ingredient a = new Ingredient();
        a.setName("Tomato");
        assertTrue(svc.addIngredient(a).success);

        Ingredient b = new Ingredient();
        b.setName("Potato");
        assertTrue(svc.addIngredient(b).success);

        // try renaming "Potato" to "Tomato" -> should fail
        Ingredient potato = svc.getIngredients().stream().filter(i -> "Potato".equals(i.getName())).findFirst().orElseThrow();
        Ingredient updated = new Ingredient();
        updated.setName("Tomato"); // dup
        IngredientService.ValidationResult vr = svc.updateIngredient(potato.getId(), updated);
        assertFalse(vr.success, "update should reject duplicate name");
    }

    @Test
    public void deleteIngredient_removesIt() {
        IngredientService svc = new IngredientService();

        Ingredient a = new Ingredient();
        a.setName("Yeast");
        assertTrue(svc.addIngredient(a).success);

        String id = svc.getIngredients().get(0).getId();
        assertTrue(svc.deleteIngredient(id), "delete should return true");

        assertTrue(svc.getIngredients().isEmpty(), "store should be empty after delete");
    }

    @Test
    public void clearEmptiesStore() {
        IngredientService svc = new IngredientService();

        Ingredient a = new Ingredient();
        a.setName("Milk");
        assertTrue(svc.addIngredient(a).success);

        svc.clear();
        assertTrue(svc.getIngredients().isEmpty(), "clear() should remove all ingredients");
    }

    @Test
    public void persistsToDiskAndReloads() {
        // 1) create service, add one ingredient
        IngredientService svc1 = new IngredientService();
        Ingredient a = new Ingredient();
        a.setName("Cocoa");
        a.setUnit("g");
        assertTrue(svc1.addIngredient(a).success);

        // 2) construct a NEW service; it should load persisted data
        IngredientService svc2 = new IngredientService();
        List<Ingredient> list = svc2.getIngredients();
        assertEquals(1, list.size(), "new service should load ingredients from disk");
        assertEquals("Cocoa", list.get(0).getName());
        assertEquals("G", list.get(0).getUnit()); // normalized
    }
}
