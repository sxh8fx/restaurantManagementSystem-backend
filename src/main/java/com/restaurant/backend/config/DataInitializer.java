package com.restaurant.backend.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.restaurant.backend.entity.ERole;
import com.restaurant.backend.entity.Role;
import com.restaurant.backend.entity.User;
import com.restaurant.backend.repository.RoleRepository;
import com.restaurant.backend.repository.UserRepository;
import com.restaurant.backend.service.TableService;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        PasswordEncoder encoder;

        @Autowired
        TableService tableService;

        @Autowired
        com.restaurant.backend.repository.TimeSlotRepository timeSlotRepository;

        @Override
        public void run(String... args) throws Exception {
                // Seed Tables
                tableService.seedTables();

                // Seed TimeSlots
                try {
                        // Define all desired slots
                        java.time.LocalTime[] startTimes = {
                                        // Breakfast
                                        java.time.LocalTime.of(8, 0), java.time.LocalTime.of(9, 0),
                                        java.time.LocalTime.of(10, 0),
                                        // Lunch
                                        java.time.LocalTime.of(12, 0), java.time.LocalTime.of(13, 0),
                                        java.time.LocalTime.of(14, 0), java.time.LocalTime.of(15, 0),
                                        // Dinner
                                        java.time.LocalTime.of(17, 0), java.time.LocalTime.of(18, 0),
                                        java.time.LocalTime.of(19, 0),
                                        java.time.LocalTime.of(20, 0), java.time.LocalTime.of(21, 0),
                                        java.time.LocalTime.of(22, 0)
                        };

                        for (java.time.LocalTime start : startTimes) {
                                if (!timeSlotRepository.existsByStartTime(start)) {
                                        timeSlotRepository.save(new com.restaurant.backend.entity.TimeSlot(null, start,
                                                        start.plusHours(1)));
                                }
                        }
                } catch (Exception e) {
                        System.err.println("Error seeding time slots: " + e.getMessage());
                        e.printStackTrace();
                }

                // Initializing Roles
                if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                        roleRepository.save(new Role(ERole.ROLE_USER));
                }

                if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                        roleRepository.save(new Role(ERole.ROLE_ADMIN));
                }

                // Initializing Admin
                // Initializing Admin - Dick Hallorann
                User admin;
                if (!userRepository.existsByUsername("Hallorann")) {
                        admin = new User("Hallorann", "shining@gmail.com", encoder.encode("redrum"), "Dick Hallorann");

                        Set<Role> roles = new HashSet<>();
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        admin.setRoles(roles);
                        userRepository.save(admin);
                        System.out.println("Admin 'Hallorann' created successfully.");
                } else {
                        // Optional: Update existing Hallorann if needed, or just log
                        System.out.println("Admin 'Hallorann' already exists.");
                }
        }
}
