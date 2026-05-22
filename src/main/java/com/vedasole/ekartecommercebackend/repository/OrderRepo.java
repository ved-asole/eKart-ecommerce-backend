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

    @Query(
            value = "SELECT TO_CHAR(o.\"create_dt\", 'YYYY-MM') AS date, SUM(o.\"total\") AS income " +
                    "FROM \"order\" o GROUP BY TO_CHAR(o.\"create_dt\", 'YYYY-MM') " +
                    "ORDER BY date"
            , nativeQuery = true
    )
    List<MonthlyIncomeDto> getTotalIncomeByMonth();
}