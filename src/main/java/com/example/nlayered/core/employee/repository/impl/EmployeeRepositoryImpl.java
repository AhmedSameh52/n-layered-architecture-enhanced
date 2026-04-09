package com.example.nlayered.core.employee.repository.impl;

import com.example.nlayered.common.enums.EmployeeStatus;
import com.example.nlayered.core.employee.entity.Employee;
import com.example.nlayered.core.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final DSLContext dsl;

    // ── Table & field definitions ─────────────────────────────────────────
    private static final Table<Record>        T             = DSL.table("employees");
    private static final Field<Long>          ID            = DSL.field("id",              Long.class);
    private static final Field<String>        FIRST_NAME    = DSL.field("first_name",      String.class);
    private static final Field<String>        LAST_NAME     = DSL.field("last_name",       String.class);
    private static final Field<String>        EMAIL         = DSL.field("email",           String.class);
    private static final Field<String>        PHONE         = DSL.field("phone",           String.class);
    private static final Field<Long>          DEPARTMENT_ID = DSL.field("department_id",   Long.class);
    private static final Field<Long>          ROLE_ID       = DSL.field("role_id",         Long.class);
    private static final Field<BigDecimal>    SALARY        = DSL.field("salary",          BigDecimal.class);
    private static final Field<LocalDate>     HIRE_DATE     = DSL.field("hire_date",       LocalDate.class);
    private static final Field<String>        STATUS        = DSL.field("status",          String.class);
    private static final Field<LocalDateTime> CREATED_AT    = DSL.field("created_at",      LocalDateTime.class);
    private static final Field<LocalDateTime> UPDATED_AT    = DSL.field("updated_at",      LocalDateTime.class);

    @Override
    public Optional<Employee> findById(Long id) {
        return dsl.select().from(T)
                .where(ID.eq(id))
                .and(STATUS.ne(EmployeeStatus.TERMINATED.name()))
                .fetchOptional()
                .map(this::toEntity);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return dsl.select().from(T)
                .where(EMAIL.equalIgnoreCase(email))
                .fetchOptional()
                .map(this::toEntity);
    }

    @Override
    public List<Employee> findAll(int offset, int limit) {
        return dsl.select().from(T)
                .where(STATUS.ne(EmployeeStatus.TERMINATED.name()))
                .orderBy(LAST_NAME.asc(), FIRST_NAME.asc())
                .limit(limit).offset(offset)
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public List<Employee> findByDepartmentId(Long departmentId, int offset, int limit) {
        return dsl.select().from(T)
                .where(DEPARTMENT_ID.eq(departmentId))
                .and(STATUS.ne(EmployeeStatus.TERMINATED.name()))
                .orderBy(LAST_NAME.asc())
                .limit(limit).offset(offset)
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public List<Employee> findByRoleId(Long roleId) {
        return dsl.select().from(T)
                .where(ROLE_ID.eq(roleId))
                .and(STATUS.eq(EmployeeStatus.ACTIVE.name()))
                .orderBy(HIRE_DATE.asc())
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public List<Employee> findByStatus(EmployeeStatus status, int offset, int limit) {
        return dsl.select().from(T)
                .where(STATUS.eq(status.name()))
                .orderBy(LAST_NAME.asc())
                .limit(limit).offset(offset)
                .fetch()
                .map(this::toEntity);
    }

    @Override
    public Employee save(Employee employee) {
        Long id = dsl.insertInto(T)
                .set(FIRST_NAME,    employee.getFirstName())
                .set(LAST_NAME,     employee.getLastName())
                .set(EMAIL,         employee.getEmail())
                .set(PHONE,         employee.getPhone())
                .set(DEPARTMENT_ID, employee.getDepartmentId())
                .set(ROLE_ID,       employee.getRoleId())
                .set(SALARY,        employee.getSalary())
                .set(HIRE_DATE,     employee.getHireDate())
                .set(STATUS,        EmployeeStatus.PROBATION.name())
                .set(CREATED_AT,    LocalDateTime.now())
                .set(UPDATED_AT,    LocalDateTime.now())
                .returningResult(ID)
                .fetchOneInto(Long.class);
        return findById(id).orElseThrow();
    }

    @Override
    public Employee update(Employee employee) {
        dsl.update(T)
                .set(FIRST_NAME,    employee.getFirstName())
                .set(LAST_NAME,     employee.getLastName())
                .set(PHONE,         employee.getPhone())
                .set(DEPARTMENT_ID, employee.getDepartmentId())
                .set(ROLE_ID,       employee.getRoleId())
                .set(SALARY,        employee.getSalary())
                .set(UPDATED_AT,    LocalDateTime.now())
                .where(ID.eq(employee.getId()))
                .execute();
        return findById(employee.getId()).orElseThrow();
    }

    @Override
    public void updateStatus(Long id, EmployeeStatus status) {
        dsl.update(T)
                .set(STATUS,     status.name())
                .set(UPDATED_AT, LocalDateTime.now())
                .where(ID.eq(id))
                .execute();
    }

    @Override
    public long count() {
        return dsl.selectCount().from(T)
                .where(STATUS.ne(EmployeeStatus.TERMINATED.name()))
                .fetchOneInto(Long.class);
    }

    @Override
    public long countByDepartmentId(Long departmentId) {
        return dsl.selectCount().from(T)
                .where(DEPARTMENT_ID.eq(departmentId))
                .and(STATUS.ne(EmployeeStatus.TERMINATED.name()))
                .fetchOneInto(Long.class);
    }

    @Override
    public boolean existsByEmail(String email) {
        return dsl.fetchExists(dsl.selectOne().from(T).where(EMAIL.equalIgnoreCase(email)));
    }

    // ── Mapping ──────────────────────────────────────────────────────────

    private Employee toEntity(Record r) {
        return Employee.builder()
                .id(r.get(ID))
                .firstName(r.get(FIRST_NAME))
                .lastName(r.get(LAST_NAME))
                .email(r.get(EMAIL))
                .phone(r.get(PHONE))
                .departmentId(r.get(DEPARTMENT_ID))
                .roleId(r.get(ROLE_ID))
                .salary(r.get(SALARY))
                .hireDate(r.get(HIRE_DATE))
                .status(EmployeeStatus.valueOf(r.get(STATUS)))
                .createdAt(r.get(CREATED_AT))
                .updatedAt(r.get(UPDATED_AT))
                .build();
    }
}