package com.restaurant.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.restaurant.backend.entity.Reservation;
import com.restaurant.backend.entity.TimeSlot;
import com.restaurant.backend.payload.response.MessageResponse;
import com.restaurant.backend.service.ReservationService;

import lombok.Data;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Reservation> getMyReservations() {
        System.out.println("DEBUG: ReservationController - getMyReservations reached for user: "
                + SecurityContextHolder.getContext().getAuthentication().getName());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reservationService.getUserReservations(username);
    }

    @GetMapping("/slots")
    public List<TimeSlot> getAllSlots() {
        return reservationService.getAllTimeSlots();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> makeReservation(@RequestBody ReservationRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            reservationService.createReservation(username, request.getTableId(), request.getDate(),
                    request.getTimeSlotId());
            return ResponseEntity.ok(new MessageResponse("Reservation successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    static class ReservationRequest {
        private Long tableId;
        private Long timeSlotId;
        private LocalDate date;

        public Long getTableId() {
            return tableId;
        }

        public void setTableId(Long tableId) {
            this.tableId = tableId;
        }

        public Long getTimeSlotId() {
            return timeSlotId;
        }

        public void setTimeSlotId(Long timeSlotId) {
            this.timeSlotId = timeSlotId;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }
}
