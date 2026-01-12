package com.restaurant.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.restaurant.backend.entity.RestaurantTable;
import com.restaurant.backend.service.TableService;
import com.restaurant.backend.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return tableService.getAllTables();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantTable addTable(@RequestBody RestaurantTable table) {
        return tableService.saveTable(table);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok(new MessageResponse("Table deleted successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantTable updateTable(@PathVariable Long id, @RequestBody RestaurantTable table) {
        RestaurantTable existingTable = tableService.getTableById(id);
        if (existingTable != null) {
            existingTable.setTableNumber(table.getTableNumber());
            existingTable.setCapacity(table.getCapacity());
            if (table.getStatus() != null) {
                existingTable.setStatus(table.getStatus());
            }
            return tableService.saveTable(existingTable);
        }
        return tableService.saveTable(table);
    }
}
