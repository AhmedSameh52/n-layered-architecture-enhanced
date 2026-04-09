package com.example.nlayered.core.order.service.impl;

import com.example.nlayered.common.clients.InventoryClient;
import com.example.nlayered.common.clients.dto.InventoryCheckRequest;
import com.example.nlayered.common.clients.dto.InventoryCheckResponse;
import com.example.nlayered.common.clients.dto.StockReservationRequest;
import com.example.nlayered.controller.dto.PagedResponse;
import com.example.nlayered.common.enums.OrderStatus;
import com.example.nlayered.common.mapper.OrderMapper;
import com.example.nlayered.controller.dto.CreateOrderRequest;
import com.example.nlayered.controller.dto.OrderResponse;
import com.example.nlayered.core.order.entity.Order;
import com.example.nlayered.core.order.entity.OrderItem;
import com.example.nlayered.core.customer.repository.CustomerRepository;
import com.example.nlayered.core.order.repository.OrderRepository;
import com.example.nlayered.core.order.service.OrderService;
import com.example.nlayered.events.dto.EventMetadata;
import com.example.nlayered.events.model.OrderCreatedEvent;
import com.example.nlayered.events.model.OrderStatusChangedEvent;
import com.example.nlayered.events.producer.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository    orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper        orderMapper;
    private final OrderEventProducer orderEventProducer;
    private final InventoryClient    inventoryClient;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // 1. Validate customer exists and is active
        customerRepository.findById(request.getCustomerId())
                .filter(c -> c.isActive())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Active customer not found: " + request.getCustomerId()));

        // 2. Check inventory availability via Feign
        verifyInventory(request);

        // 3. Build order items and total
        List<OrderItem> items = buildItems(request);
        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Persist order
        Order order = orderRepository.save(Order.builder()
                .customerId(request.getCustomerId())
                .processedByEmployeeId(request.getProcessedByEmployeeId())
                .totalAmount(total)
                .notes(request.getNotes())
                .status(OrderStatus.PENDING)
                .build());

        // 5. Persist order items
        List<OrderItem> persistedItems = items.stream()
                .map(i -> i.toBuilder().orderId(order.getId()).build())
                .toList();
        orderRepository.saveItems(persistedItems);

        // 6. Reserve stock
        reserveStock(order.getId(), request);

        // 7. Publish event
        publishOrderCreated(order, persistedItems);

        log.info("Created order id={} customerId={} total={}", order.getId(), order.getCustomerId(), total);

        return orderMapper.toResponse(order.toBuilder().items(persistedItems).build());
    }

    @Override
    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    @Override
    public PagedResponse<OrderResponse> listByCustomer(Long customerId, int page, int size) {
        int offset = page * size;
        List<Order> orders = orderRepository.findByCustomerId(customerId, offset, size);
        long total = orderRepository.countByCustomerId(customerId);
        return PagedResponse.of(orderMapper.toResponseList(orders), page, size, total);
    }

    @Override
    public PagedResponse<OrderResponse> listByStatus(OrderStatus status, int page, int size) {
        int offset = page * size;
        List<Order> orders = orderRepository.findByStatus(status, offset, size);
        long total = orderRepository.countByStatus(status);
        return PagedResponse.of(orderMapper.toResponseList(orders), page, size, total);
    }

    @Override
    public OrderResponse advanceStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus next = nextStatus(order.getStatus());
        if (!order.getStatus().canTransitionTo(next)) {
            throw new IllegalStateException(
                    "Cannot transition order %d from %s to %s".formatted(orderId, order.getStatus(), next));
        }

        Order updated = orderRepository.updateStatus(orderId, next);
        publishStatusChanged(updated, order.getStatus());
        return orderMapper.toResponse(updated);
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!order.isCancellable()) {
            throw new IllegalStateException(
                    "Order %d cannot be cancelled in status %s".formatted(orderId, order.getStatus()));
        }

        Order updated = orderRepository.updateStatus(orderId, OrderStatus.CANCELLED);
        publishStatusChanged(updated, order.getStatus());
        log.info("Cancelled order id={}", orderId);
        return orderMapper.toResponse(updated);
    }

    @Override
    public OrderResponse assignEmployee(Long orderId, Long employeeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        Order updated = orderRepository.updateStatus(orderId, order.getStatus()); // refresh
        return orderMapper.toResponse(updated.toBuilder().processedByEmployeeId(employeeId).build());
    }

    // ── Private helpers ──────────────────────────────────────────────────

    private void verifyInventory(CreateOrderRequest request) {
        List<InventoryCheckRequest.LineItem> lines = request.getItems().stream()
                .map(i -> InventoryCheckRequest.LineItem.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .build())
                .toList();

        InventoryCheckResponse response = inventoryClient.checkAvailability(
                InventoryCheckRequest.builder().items(lines).build());

        if (!response.isAllAvailable()) {
            String unavailable = response.getUnavailableItems().stream()
                    .map(u -> "productId=%d (requested=%d, available=%d)"
                            .formatted(u.getProductId(), u.getRequested(), u.getAvailable()))
                    .reduce("", (a, b) -> a + "; " + b);
            throw new IllegalArgumentException("Insufficient stock: " + unavailable);
        }
    }

    private void reserveStock(Long orderId, CreateOrderRequest request) {
        List<StockReservationRequest.LineItem> lines = request.getItems().stream()
                .map(i -> StockReservationRequest.LineItem.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .build())
                .toList();
        inventoryClient.reserveStock(StockReservationRequest.builder()
                .orderId(orderId)
                .items(lines)
                .build());
    }

    private List<OrderItem> buildItems(CreateOrderRequest request) {
        return request.getItems().stream()
                .map(i -> {
                    BigDecimal total = i.getUnitPrice()
                            .multiply(BigDecimal.valueOf(i.getQuantity()));
                    return OrderItem.builder()
                            .productId(i.getProductId())
                            .productName(i.getProductName())
                            .quantity(i.getQuantity())
                            .unitPrice(i.getUnitPrice())
                            .totalPrice(total)
                            .build();
                })
                .toList();
    }

    private OrderStatus nextStatus(OrderStatus current) {
        return switch (current) {
            case PENDING    -> OrderStatus.CONFIRMED;
            case CONFIRMED  -> OrderStatus.PROCESSING;
            case PROCESSING -> OrderStatus.SHIPPED;
            case SHIPPED    -> OrderStatus.DELIVERED;
            default -> throw new IllegalStateException("No next status for " + current);
        };
    }

    private void publishOrderCreated(Order order, List<OrderItem> items) {
        List<OrderCreatedEvent.LineItem> lines = items.stream()
                .map(i -> OrderCreatedEvent.LineItem.builder()
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .build())
                .toList();

        orderEventProducer.publishOrderCreated(OrderCreatedEvent.builder()
                .metadata(EventMetadata.of("ORDER_CREATED"))
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .totalAmount(order.getTotalAmount())
                .items(lines)
                .build());
    }

    private void publishStatusChanged(Order updated, OrderStatus previous) {
        orderEventProducer.publishStatusChanged(OrderStatusChangedEvent.builder()
                .metadata(EventMetadata.of("ORDER_STATUS_CHANGED"))
                .orderId(updated.getId())
                .customerId(updated.getCustomerId())
                .previousStatus(previous)
                .newStatus(updated.getStatus())
                .build());
    }
}