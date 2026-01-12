package com.restaurant.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.backend.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDate(LocalDate date);

    List<Reservation> findByUser_Id(Long userId);

    // Check for existing confirmed reservation for table at date/time
    boolean existsByTable_IdAndDateAndTimeSlot_IdAndStatus(Long tableId, LocalDate date, Long timeSlotId,
            String status);
}
