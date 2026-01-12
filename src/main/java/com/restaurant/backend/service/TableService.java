package com.restaurant.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restaurant.backend.entity.RestaurantTable;
import com.restaurant.backend.repository.TableRepository;

@Service
public class TableService {
    @Autowired
    private TableRepository tableRepository;

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestaurantTable saveTable(RestaurantTable table) {
        if (tableRepository.existsByTableNumber(table.getTableNumber()) && table.getId() == null) {
            throw new RuntimeException("Table number already exists!");
        }
        return tableRepository.save(table);
    }

    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    public RestaurantTable getTableById(Long id) {
        return tableRepository.findById(id).orElseThrow(() -> new RuntimeException("Table not found"));
    }

    // Initial seeding logic can be added here or in DataInitializer
    public void seedTables() {
        if (tableRepository.count() == 0) {
            // Tables 1-8: 4-seater
            for (int i = 1; i <= 8; i++) {
                tableRepository.save(new RestaurantTable(null, i, 4, "AVAILABLE"));
            }
            // Tables 9-12: 6-seater
            for (int i = 9; i <= 12; i++) {
                tableRepository.save(new RestaurantTable(null, i, 6, "AVAILABLE"));
            }
            // Tables 13-15: 2-seater
            for (int i = 13; i <= 15; i++) {
                tableRepository.save(new RestaurantTable(null, i, 2, "AVAILABLE"));
            }
        }
    }
}
