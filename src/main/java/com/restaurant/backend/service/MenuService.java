package com.restaurant.backend.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.backend.entity.MenuItem;
import com.restaurant.backend.entity.OrderItem;
import com.restaurant.backend.repository.MenuItemRepository;
import com.restaurant.backend.repository.OrderItemRepository;

@Service
public class MenuService {
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<MenuItem> getAllItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAvailableItems() {
        return menuItemRepository.findByAvailable(true);
    }

    public MenuItem saveItem(MenuItem item) {
        return menuItemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Nullify references in OrderItems to prevent foreign key constraint violation
        List<OrderItem> orderItems = orderItemRepository.findByMenuItem(menuItem);
        for (OrderItem orderItem : orderItems) {
            orderItem.setMenuItem(null);
            orderItemRepository.save(orderItem);
        }

        menuItemRepository.deleteById(id);
    }

    public MenuItem getItem(Long id) {
        return menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Transactional
    public void fixLegacyCategories() {
        List<MenuItem> allItems = menuItemRepository.findAll();
        for (MenuItem item : allItems) {
            String oldCat = item.getCategory();
            if (oldCat == null)
                oldCat = "";
            String lower = oldCat.toLowerCase();

            // "Starters, Main Course, Rice & Biriyani, Breads, Egg Dishes, Sides, Drinks,
            // Deserts, Water"

            String newCat = oldCat; // Default to keep if no match, or force? User said "put it in any of these"

            if (lower.contains("starter") || lower.contains("appetizer") || lower.contains("soup")) {
                newCat = "Starters";
            } else if (lower.contains("rice") || lower.contains("biryani") || lower.contains("pulao")) {
                newCat = "Rice & Biriyani";
            } else if (lower.contains("bread") || lower.contains("roti") || lower.contains("naan")
                    || lower.contains("paratha") || lower.contains("kulcha")) {
                newCat = "Breads";
            } else if (lower.contains("egg") || lower.contains("omelet")) {
                newCat = "Egg Dishes";
            } else if (lower.contains("drink") || lower.contains("beverage") || lower.contains("juice")
                    || lower.contains("soda") || lower.contains("coffee") || lower.contains("tea")) {
                newCat = "Drinks";
            } else if (lower.contains("water")) {
                newCat = "Water";
            } else if (lower.contains("desert") || lower.contains("dessert") || lower.contains("sweet")
                    || lower.contains("ice cream") || lower.contains("cake")) {
                newCat = "Deserts";
            } else if (lower.contains("side") || lower.contains("salad") || lower.contains("raita")
                    || lower.contains("papad")) {
                newCat = "Sides";
            } else if (lower.contains("main") || lower.contains("curry") || lower.contains("gravy")
                    || lower.contains("chicken") || lower.contains("mutton") || lower.contains("paneer")
                    || lower.contains("veg") || lower.contains("dal")) {
                newCat = "Main Course";
            } else {
                // Determine fallback based on keywords?
                // If it didn't match any above, and it's not one of the valid ones...
                // Let's check if it's already valid
                if (!isValidCategory(oldCat)) {
                    newCat = "Main Course"; // Fallback
                }
            }

            if (!newCat.equals(oldCat)) {
                item.setCategory(newCat);
                menuItemRepository.save(item);
                System.out.println("Migrated: " + item.getName() + " [" + oldCat + "] -> [" + newCat + "]");
            }
        }
    }

    private boolean isValidCategory(String cat) {
        List<String> valid = List.of("Starters", "Main Course", "Rice & Biriyani", "Breads", "Egg Dishes", "Sides",
                "Drinks", "Deserts", "Water");
        return valid.contains(cat);
    }
}
