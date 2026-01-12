package com.restaurant.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/debug-auth")
    public Map<String, Object> debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> info = new HashMap<>();
        if (auth != null) {
            info.put("principal", auth.getPrincipal());
            info.put("authorities", auth.getAuthorities());
            info.put("name", auth.getName());
            info.put("isAuthenticated", auth.isAuthenticated());
        } else {
            info.put("error", "No authentication found in SecurityContext");
        }
        return info;
    }

    @GetMapping("/admin-check")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminCheck() {
        return "You are definitively an ADMIN.";
    }
}
