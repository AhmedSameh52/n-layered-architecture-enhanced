package com.example.nlayered.core.employee.repository.impl;

import com.example.nlayered.core.employee.entity.Department;
import com.example.nlayered.core.employee.repository.DepartmentRepository;
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
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final DSLContext dsl;

    // ── Table & field definitions ─────────────────────────────────────────
    private static final Table<Record>        T          = DSL.table("departments");
    private static final Field<Long>          ID         = DSL.field("id",                  Long.class);
    private static final Field<String>        NAME       = DSL.field("name",                String.class);
    private static final Field<String>        DESCRIPTION= DSL.field("description",         String.class);
    private static final Field<Long>          MANAGER_ID = DSL.field("manager_employee_id", Long.class);
    private static final Field<LocalDateTime> CREATED_AT = DSL.field("created_at",          LocalDateTime.class);
    private static final Field<LocalDateTime> UPDATED_AT = DSL.field("updated_at",          LocalDateTime.class);

    @Override
    public Optional<Department> findById(Long id) {
        return dsl.select().from(T)
                .where(ID.eq(id))
                .fetchOptional()
                .map(this::toEntity);
    }

    @Override
    public Optional<Department> findByName(String name) {
        return dsl.select().from(T)
                .where(NAME.equalIgnoreCase(name))
                .fetchOptional()
                .map(this::toEntity);
    }

    @Override
    public List<Department> findAll() {
        return dsl.select().from(T)
                .orderBy(NAME.asc())
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public Department save(Department department) {
        Long id = dsl.insertInto(T)
                .set(NAME,        department.getName())
                .set(DESCRIPTION, department.getDescription())
                .set(CREATED_AT,  LocalDateTime.now())
                .set(UPDATED_AT,  LocalDateTime.now())
                .returningResult(ID)
                .fetchOneInto(Long.class);
        return findById(id).orElseThrow();
    }

    @Override
    public Department update(Department department) {
        dsl.update(T)
                .set(NAME,        department.getName())
                .set(DESCRIPTION, department.getDescription())
                .set(UPDATED_AT,  LocalDateTime.now())
                .where(ID.eq(department.getId()))
                .execute();
        return findById(department.getId()).orElseThrow();
    }

    @Override
    public void assignManager(Long departmentId, Long employeeId) {
        dsl.update(T)
                .set(MANAGER_ID, employeeId)
                .set(UPDATED_AT, LocalDateTime.now())
                .where(ID.eq(departmentId))
                .execute();
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(dsl.selectOne().from(T).where(ID.eq(id)));
    }

    @Override
    public boolean existsByName(String name) {
        return dsl.fetchExists(dsl.selectOne().from(T).where(NAME.equalIgnoreCase(name)));
    }

    // ── Mapping ──────────────────────────────────────────────────────────

    private Department toEntity(Record r) {
        return Department.builder()
                .id(r.get(ID))
                .name(r.get(NAME))
                .description(r.get(DESCRIPTION))
                .managerEmployeeId(r.get(MANAGER_ID))
                .createdAt(r.get(CREATED_AT))
                .updatedAt(r.get(UPDATED_AT))
                .build();
    }
}