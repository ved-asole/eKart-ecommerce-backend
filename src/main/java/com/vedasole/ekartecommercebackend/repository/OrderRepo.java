package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Order;
import com.vedasole.ekartecommercebackend.payload.MonthlyIncomeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomer_CustomerId(Long customerId);
    Page<Order> findAllByCustomer_CustomerId(Pageable pageable, Long customerId);

    @Query(value = "SELECT COALESCE(SUM(o.\"total\"), 0) FROM \"order\" o", nativeQuery = true)
    Double getTotalIncome();

    @Query(value = """
    SELECT new com.vedasole.ekartecommercebackend.payload.MonthlyIncomeDto(
        CONCAT(CAST(YEAR(o.createdAt) AS string), '-', LPAD(CAST(MONTH(o.createdAt) AS string), 2, '0')),
        SUM(o.total)
    )
    FROM Order o
    GROUP BY CONCAT(CAST(YEAR(o.createdAt) AS string), '-', LPAD(CAST(MONTH(o.createdAt) AS string), 2, '0'))
    ORDER BY CONCAT(CAST(YEAR(o.createdAt) AS string), '-', LPAD(CAST(MONTH(o.createdAt) AS string), 2, '0')) ASC
    """)
    List<MonthlyIncomeDto> getTotalIncomeByMonth();
}