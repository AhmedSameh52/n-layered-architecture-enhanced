package com.example.nlayered.core.order.repository.impl;

import com.example.nlayered.common.enums.OrderStatus;
import com.example.nlayered.core.order.entity.Order;
import com.example.nlayered.core.order.entity.OrderItem;
import com.example.nlayered.core.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final DSLContext dsl;

    // ── orders table ──────────────────────────────────────────────────────
    private static final Table<Record>        O          = DSL.table("orders");
    private static final Field<Long>          O_ID       = DSL.field("id",                       Long.class);
    private static final Field<Long>          CUST_ID    = DSL.field("customer_id",               Long.class);
    private static final Field<Long>          EMP_ID     = DSL.field("processed_by_employee_id",  Long.class);
    private static final Field<String>        O_STATUS   = DSL.field("status",                   String.class);
    private static final Field<BigDecimal>    TOTAL      = DSL.field("total_amount",              BigDecimal.class);
    private static final Field<String>        NOTES      = DSL.field("notes",                    String.class);
    private static final Field<LocalDateTime> O_CREATED  = DSL.field("created_at",               LocalDateTime.class);
    private static final Field<LocalDateTime> O_UPDATED  = DSL.field("updated_at",               LocalDateTime.class);

    // ── order_items table ─────────────────────────────────────────────────
    private static final Table<Record>        I          = DSL.table("order_items");
    private static final Field<Long>          I_ID       = DSL.field("id",           Long.class);
    private static final Field<Long>          I_ORDER    = DSL.field("order_id",     Long.class);
    private static final Field<Long>          I_PRODUCT  = DSL.field("product_id",   Long.class);
    private static final Field<String>        I_NAME     = DSL.field("product_name", String.class);
    private static final Field<Integer>       I_QTY      = DSL.field("quantity",     Integer.class);
    private static final Field<BigDecimal>    I_UNIT     = DSL.field("unit_price",   BigDecimal.class);
    private static final Field<BigDecimal>    I_TOTAL    = DSL.field("total_price",  BigDecimal.class);
    private static final Field<LocalDateTime> I_CREATED  = DSL.field("created_at",  LocalDateTime.class);

    @Override
    public Optional<Order> findById(Long id) {
        return dsl.select().from(O)
                .where(O_ID.eq(id))
                .fetchOptional()
                .map(r -> toEntity(r, findItemsByOrderId(id)));
    }

    @Override
    public List<Order> findByCustomerId(Long customerId, int offset, int limit) {
        return dsl.select().from(O)
                .where(CUST_ID.eq(customerId))
                .orderBy(O_CREATED.desc())
                .limit(limit).offset(offset)
                .fetch()
                .map(r -> toEntity(r, List.of()));
    }

    @Override
    public List<Order> findByStatus(OrderStatus status, int offset, int limit) {
        return dsl.select().from(O)
                .where(O_STATUS.eq(status.name()))
                .orderBy(O_CREATED.desc())
                .limit(limit).offset(offset)
                .fetch()
                .map(r -> toEntity(r, List.of()));
    }

    @Override
    public List<Order> findByEmployeeId(Long employeeId, int offset, int limit) {
        return dsl.select().from(O)
                .where(EMP_ID.eq(employeeId))
                .orderBy(O_CREATED.desc())
                .limit(limit).offset(offset)
                .fetch()
                .map(r -> toEntity(r, List.of()));
    }

    @Override
    public Order save(Order order) {
        Long id = dsl.insertInto(O)
                .set(CUST_ID,   order.getCustomerId())
                .set(EMP_ID,    order.getProcessedByEmployeeId())
                .set(O_STATUS,  OrderStatus.PENDING.name())
                .set(TOTAL,     order.getTotalAmount())
                .set(NOTES,     order.getNotes())
                .set(O_CREATED, LocalDateTime.now())
                .set(O_UPDATED, LocalDateTime.now())
                .returningResult(O_ID)
                .fetchOneInto(Long.class);
        return findById(id).orElseThrow();
    }

    @Override
    public void saveItems(List<OrderItem> items) {
        var batch = items.stream()
                .map(item -> dsl.insertInto(I)
                        .set(I_ORDER,   item.getOrderId())
                        .set(I_PRODUCT, item.getProductId())
                        .set(I_NAME,    item.getProductName())
                        .set(I_QTY,     item.getQuantity())
                        .set(I_UNIT,    item.getUnitPrice())
                        .set(I_TOTAL,   item.getTotalPrice())
                        .set(I_CREATED, LocalDateTime.now()))
                .toList();
        dsl.batch(batch).execute();
    }

    @Override
    public Order updateStatus(Long orderId, OrderStatus status) {
        dsl.update(O)
                .set(O_STATUS,  status.name())
                .set(O_UPDATED, LocalDateTime.now())
                .where(O_ID.eq(orderId))
                .execute();
        return findById(orderId).orElseThrow();
    }

    @Override
    public List<OrderItem> findItemsByOrderId(Long orderId) {
        return dsl.select().from(I)
                .where(I_ORDER.eq(orderId))
                .orderBy(I_ID.asc())
                .fetch()
                .map(this::toItemEntity);
    }

    @Override
    public long countByCustomerId(Long customerId) {
        return dsl.selectCount().from(O)
                .where(CUST_ID.eq(customerId))
                .fetchOneInto(Long.class);
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return dsl.selectCount().from(O)
                .where(O_STATUS.eq(status.name()))
                .fetchOneInto(Long.class);
    }

    // ── Mapping ──────────────────────────────────────────────────────────

    private Order toEntity(Record r, List<OrderItem> items) {
        return Order.builder()
                .id(r.get(O_ID))
                .customerId(r.get(CUST_ID))
                .processedByEmployeeId(r.get(EMP_ID))
                .status(OrderStatus.valueOf(r.get(O_STATUS)))
                .totalAmount(r.get(TOTAL))
                .notes(r.get(NOTES))
                .createdAt(r.get(O_CREATED))
                .updatedAt(r.get(O_UPDATED))
                .items(items)
                .build();
    }

    private OrderItem toItemEntity(Record r) {
        return OrderItem.builder()
                .id(r.get(I_ID))
                .orderId(r.get(I_ORDER))
                .productId(r.get(I_PRODUCT))
                .productName(r.get(I_NAME))
                .quantity(r.get(I_QTY))
                .unitPrice(r.get(I_UNIT))
                .totalPrice(r.get(I_TOTAL))
                .createdAt(r.get(I_CREATED))
                .build();
    }
}