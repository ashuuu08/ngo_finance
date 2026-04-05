package com.ngo_finance.ngo_finance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId;

    private String category;
    
    private BigDecimal amount;
    
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timeAndDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    private String updatedBy;
}
