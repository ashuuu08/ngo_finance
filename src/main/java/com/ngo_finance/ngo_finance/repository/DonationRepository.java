package com.ngo_finance.ngo_finance.repository;

import com.ngo_finance.ngo_finance.entity.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    
    // Gets total donations within an exact date range
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.dateAndTime BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Gets the lifetime total donations
    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d")
    java.math.BigDecimal sumTotalAmount();

    // Gets the list of 10 most recently placed donations
    List<Donation> findTop10ByOrderByDateAndTimeDesc();

    // Gets all donations linked safely to a specific user
    List<Donation> findByUserIdOrderByDateAndTimeDesc(Long userId);

    // Securely groups by the attached user details accumulating their complete donation lifecycle
    @Query(value = "SELECT d.user.id, d.user.fullName, d.user.email, d.user.phone, COUNT(d), COALESCE(SUM(d.amount), 0) " +
           "FROM Donation d WHERE d.user IS NOT NULL " +
           "GROUP BY d.user.id, d.user.fullName, d.user.email, d.user.phone",
           countQuery = "SELECT COUNT(DISTINCT d.user.id) FROM Donation d WHERE d.user IS NOT NULL")
    Page<Object[]> findDonorsAggregateData(Pageable pageable);

    // Pulls down the complete global donation ledger ordered by absolute chronological latest with strict pagination boundaries
    Page<Donation> findAllByOrderByDateAndTimeDesc(Pageable pageable);
}
