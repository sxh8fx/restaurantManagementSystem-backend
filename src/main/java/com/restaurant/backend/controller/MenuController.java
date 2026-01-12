package com.restaurant.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.restaurant.backend.entity.MenuItem;
import com.restaurant.backend.payload.response.MessageResponse;
import com.restaurant.backend.service.MenuService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping
    public List<MenuItem> getMenu(@RequestParam(required = false) boolean all) {
        if (all) {
            return menuService.getAllItems();
        }
        return menuService.getAvailableItems();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItem addMenuItem(@RequestBody MenuItem item) {
        return menuService.saveItem(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MenuItem updateMenuItem(@PathVariable Long id, @RequestBody MenuItem item) {
        MenuItem existing = menuService.getItem(id);
        existing.setName(item.getName());
        existing.setDescription(item.getDescription());
        existing.setPrice(item.getPrice());
        existing.setAvailable(item.isAvailable());
        existing.setCategory(item.getCategory());
        existing.setImageUrl(item.getImageUrl());
        return menuService.saveItem(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteItem(id);
        return ResponseEntity.ok(new MessageResponse("Menu item deleted successfully"));
    }

}
