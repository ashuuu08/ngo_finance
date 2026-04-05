package com.ngo_finance.ngo_finance.repository;

import com.ngo_finance.ngo_finance.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.timeAndDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    java.math.BigDecimal sumTotalAmount();

    // Aggregates uniquely categorised expenses within specific timeline bounds
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.timeAndDate BETWEEN :startDate AND :endDate GROUP BY e.category")
    List<Object[]> sumAmountByCategoryAndDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> sumAmountByCategory();

    // Pulls down the complete global expense ledger logically grouped with absolute pagination constraints
    Page<Expense> findAllByOrderByTimeAndDateDesc(Pageable pageable);
}
