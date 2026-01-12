package com.restaurant.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.backend.entity.Reservation;
import com.restaurant.backend.entity.RestaurantTable;
import com.restaurant.backend.entity.TimeSlot;
import com.restaurant.backend.entity.User;
import com.restaurant.backend.repository.ReservationRepository;
import com.restaurant.backend.repository.TableRepository;
import com.restaurant.backend.repository.TimeSlotRepository;
import com.restaurant.backend.repository.UserRepository;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getUserReservations(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return reservationRepository.findByUser_Id(user.getId());
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    @Transactional
    public Reservation createReservation(String username, Long tableId, LocalDate date, Long timeSlotId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        RestaurantTable table = tableRepository.findById(tableId).orElseThrow();
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId).orElseThrow();

        // 1. Check if date is within next 3 days
        LocalDate today = LocalDate.now();
        if (date.isBefore(today) || date.isAfter(today.plusDays(3))) {
            throw new RuntimeException("Reservations only allowed for upcoming 3 days");
        }

        // 2. Check availability
        if (reservationRepository.existsByTable_IdAndDateAndTimeSlot_IdAndStatus(tableId, date, timeSlotId,
                "CONFIRMED")) {
            throw new RuntimeException("Table is already booked");
        }

        Reservation reservation = new Reservation(user, table, date, timeSlot);
        return reservationRepository.save(reservation);
    }

    // Additional logic for "Table availability updates in real time" would involve
    // WebSocket broadcasts here.
    // For now, simple CRUD.
}
