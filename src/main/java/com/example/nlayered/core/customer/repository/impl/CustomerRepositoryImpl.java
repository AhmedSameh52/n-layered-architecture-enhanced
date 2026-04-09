package com.example.nlayered.core.customer.repository.impl;

import com.example.nlayered.common.enums.CustomerStatus;
import com.example.nlayered.core.customer.entity.Customer;
import com.example.nlayered.core.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final DSLContext dsl;

    // ── Table & field definitions (dynamic DSL — no codegen required) ────
    private static final Table<Record>        T          = DSL.table("customers");
    private static final Field<Long>          ID         = DSL.field("id",          Long.class);
    private static final Field<String>        FIRST_NAME = DSL.field("first_name",  String.class);
    private static final Field<String>        LAST_NAME  = DSL.field("last_name",   String.class);
    private static final Field<String>        EMAIL      = DSL.field("email",        String.class);
    private static final Field<String>        PHONE      = DSL.field("phone",        String.class);
    private static final Field<String>        ADDRESS    = DSL.field("address",      String.class);
    private static final Field<String>        STATUS     = DSL.field("status",       String.class);
    private static final Field<LocalDateTime> CREATED_AT = DSL.field("created_at",  LocalDateTime.class);
    private static final Field<LocalDateTime> UPDATED_AT = DSL.field("updated_at",  LocalDateTime.class);

    @Override
    public Optional<Customer> findById(Long id) {
        return dsl.select().from(T)
                .where(ID.eq(id))
                .and(STATUS.ne(CustomerStatus.DELETED.name()))
                .fetchOptional()
                .map(this::toEntity);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return dsl.select().from(T)
                .where(EMAIL.equalIgnoreCase(email))
                .fetchOptional()
                .map(this::toEntity);
    }

    @Override
    public List<Customer> findAll(int offset, int limit) {
        return dsl.select().from(T)
                .where(STATUS.ne(CustomerStatus.DELETED.name()))
                .orderBy(CREATED_AT.desc())
                .limit(limit).offset(offset)
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public List<Customer> findByStatus(CustomerStatus status, int offset, int limit) {
        return dsl.select().from(T)
                .where(STATUS.eq(status.name()))
                .orderBy(CREATED_AT.desc())
                .limit(limit).offset(offset)
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public List<Customer> searchByName(String namePart, int offset, int limit) {
        String pattern = "%" + namePart.toLowerCase() + "%";
        return dsl.select().from(T)
                .where(STATUS.ne(CustomerStatus.DELETED.name()))
                .and(DSL.lower(FIRST_NAME).like(pattern)
                        .or(DSL.lower(LAST_NAME).like(pattern)))
                .orderBy(LAST_NAME.asc(), FIRST_NAME.asc())
                .limit(limit).offset(offset)
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public Customer save(Customer customer) {
        Long id = dsl.insertInto(T)
                .set(FIRST_NAME, customer.getFirstName())
                .set(LAST_NAME,  customer.getLastName())
                .set(EMAIL,      customer.getEmail())
                .set(PHONE,      customer.getPhone())
                .set(ADDRESS,    customer.getAddress())
                .set(STATUS,     CustomerStatus.ACTIVE.name())
                .set(CREATED_AT, LocalDateTime.now())
                .set(UPDATED_AT, LocalDateTime.now())
                .returningResult(ID)
                .fetchOneInto(Long.class);
        return findById(id).orElseThrow();
    }

    @Override
    public Customer update(Customer customer) {
        dsl.update(T)
                .set(FIRST_NAME, customer.getFirstName())
                .set(LAST_NAME,  customer.getLastName())
                .set(PHONE,      customer.getPhone())
                .set(ADDRESS,    customer.getAddress())
                .set(STATUS,     customer.getStatus().name())
                .set(UPDATED_AT, LocalDateTime.now())
                .where(ID.eq(customer.getId()))
                .execute();
        return findById(customer.getId()).orElseThrow();
    }

    @Override
    public void deleteById(Long id) {
        dsl.update(T)
                .set(STATUS,     CustomerStatus.DELETED.name())
                .set(UPDATED_AT, LocalDateTime.now())
                .where(ID.eq(id))
                .execute();
    }

    @Override
    public long count() {
        return dsl.selectCount().from(T)
                .where(STATUS.ne(CustomerStatus.DELETED.name()))
                .fetchOneInto(Long.class);
    }

    @Override
    public long countByStatus(CustomerStatus status) {
        return dsl.selectCount().from(T)
                .where(STATUS.eq(status.name()))
                .fetchOneInto(Long.class);
    }

    @Override
    public boolean existsByEmail(String email) {
        return dsl.fetchExists(dsl.selectOne().from(T).where(EMAIL.equalIgnoreCase(email)));
    }

    // ── Mapping ──────────────────────────────────────────────────────────

    private Customer toEntity(Record r) {
        return Customer.builder()
                .id(r.get(ID))
                .firstName(r.get(FIRST_NAME))
                .lastName(r.get(LAST_NAME))
                .email(r.get(EMAIL))
                .phone(r.get(PHONE))
                .address(r.get(ADDRESS))
                .status(CustomerStatus.valueOf(r.get(STATUS)))
                .createdAt(r.get(CREATED_AT))
                .updatedAt(r.get(UPDATED_AT))
                .build();
    }
}