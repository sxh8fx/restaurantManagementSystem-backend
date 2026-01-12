package com.restaurant.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.backend.entity.Bill;
import com.restaurant.backend.entity.MenuItem;
import com.restaurant.backend.entity.Order;
import com.restaurant.backend.entity.OrderItem;
import com.restaurant.backend.entity.User;
import com.restaurant.backend.repository.BillRepository;
import com.restaurant.backend.repository.MenuItemRepository;
import com.restaurant.backend.repository.OrderItemRepository;
import com.restaurant.backend.repository.OrderRepository;
import com.restaurant.backend.repository.UserRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    @Autowired
    private com.restaurant.backend.repository.ReservationRepository reservationRepository;

    @Transactional
    public Order createOrder(String username, List<OrderItemRequest> itemRequests, Long reservationId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("ORDERED");

        if (reservationId != null) {
            com.restaurant.backend.entity.Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));
            order.setReservation(reservation);
        }

        BigDecimal subTotal = BigDecimal.ZERO;

        for (OrderItemRequest req : itemRequests) {
            MenuItem item = menuItemRepository.findById(req.getMenuItemId()).orElseThrow();
            BigDecimal lineTotal = item.getPrice().multiply(new BigDecimal(req.getQuantity()));
            subTotal = subTotal.add(lineTotal);

            OrderItem orderItem = new OrderItem(order, item, req.getQuantity(), item.getPrice());
            order.getItems().add(orderItem);
        }

        // Calculate Tax (5%)
        BigDecimal taxRate = new BigDecimal("0.05");
        BigDecimal tax = subTotal.multiply(taxRate);
        BigDecimal finalTotal = subTotal.add(tax);

        order.setTaxAmount(tax);
        order.setTotalAmount(finalTotal);

        Order savedOrder = orderRepository.save(order);

        // Broadcast
        messagingTemplate.convertAndSend("/topic/admin/orders", savedOrder);

        return savedOrder;
    }

    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        messagingTemplate.convertAndSend("/topic/orders/" + orderId, updatedOrder);
        messagingTemplate.convertAndSend("/topic/admin/orders/update", updatedOrder);

        return updatedOrder;
    }

    @Transactional
    public Order updateItemStatus(Long itemId, String status) {
        OrderItem item = orderItemRepository.findById(itemId).orElseThrow();
        item.setStatus(status);
        orderItemRepository.save(item);

        // Broadcast update (sending whole order might be heavy but safest for
        // consistency)
        Order order = item.getOrder();
        messagingTemplate.convertAndSend("/topic/orders/" + order.getId(), order);
        messagingTemplate.convertAndSend("/topic/admin/orders/update", order);

        checkOrderCompletion(order);

        return order;
    }

    private void checkOrderCompletion(Order order) {
        // If all items are SERVED, generate Bill
        boolean allServed = order.getItems().stream()
                .allMatch(i -> "SERVED".equals(i.getStatus()) || "CANCELLED".equals(i.getStatus()));

        if (allServed && billRepository.findByOrder_Id(order.getId()).isEmpty()) {
            Bill bill = new Bill(order, order.getTotalAmount());
            billRepository.save(bill);

            order.setStatus("COMPLETED"); // Or "SERVED", "BILLED"
            orderRepository.save(order);

            // Notify Billing Generated
            messagingTemplate.convertAndSend("/topic/orders/" + order.getId(), order);
        }
    }

    public static class OrderItemRequest {
        private Long menuItemId;
        private Integer quantity;

        public Long getMenuItemId() {
            return menuItemId;
        }

        public void setMenuItemId(Long menuItemId) {
            this.menuItemId = menuItemId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
