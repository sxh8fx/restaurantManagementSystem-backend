package com.restaurant.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({ "password", "roles", "hibernateLazyInitializer", "handler" })
    private User user;

    @ManyToOne
    @JoinColumn(name = "table_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private RestaurantTable table;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private TimeSlot timeSlot;

    // PENDING, CONFIRMED, EXPIRED, CANCELLED, CHECKED_IN
    private String status = "CONFIRMED";

    public Reservation() {
    }

    public Reservation(User user, RestaurantTable table, LocalDate date, TimeSlot timeSlot) {
        this.user = user;
        this.table = table;
        this.date = date;
        this.timeSlot = timeSlot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RestaurantTable getTable() {
        return table;
    }

    public void setTable(RestaurantTable table) {
        this.table = table;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getTableId() {
        return table != null ? table.getId() : null;
    }

    public Long getTimeSlotId() {
        return timeSlot != null ? timeSlot.getId() : null;
    }
}
