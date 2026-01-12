package com.restaurant.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.restaurant.backend.entity.Order;
import com.restaurant.backend.payload.response.MessageResponse;
import com.restaurant.backend.service.OrderService;

import lombok.Data;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        System.out.println("DEBUG: OrderController - getAllOrders reached!");
        return orderService.getAllOrders();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Order> getMyOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.getUserOrders(username);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.createOrder(username, request.getItems(), request.getReservationId());
    }

    public static class CreateOrderRequest {
        private Long reservationId;
        private List<OrderService.OrderItemRequest> items;

        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }

        public List<OrderService.OrderItemRequest> getItems() {
            return items;
        }

        public void setItems(List<OrderService.OrderItemRequest> items) {
            this.items = items;
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Order updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        return orderService.updateStatus(orderId, status);
    }

    @PutMapping("/items/{itemId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateItemStatus(@PathVariable Long itemId, @RequestParam String status) {
        return orderService.updateItemStatus(itemId, status);
    }
}
