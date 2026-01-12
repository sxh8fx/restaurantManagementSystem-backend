package com.restaurant.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.restaurant.backend.service.MenuService;

@Component
public class DataMigration implements CommandLineRunner {

    @Autowired
    private MenuService menuService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Data Migration: Standardizing Menu Categories...");
        menuService.fixLegacyCategories();
        System.out.println("Data Migration Completed.");
    }
}
