package com.bankease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "account_from")
    private Account accountFrom;

    @ManyToOne
    @JoinColumn(name = "account_to")
    private Account accountTo;

    private double amount;
    private String type;
    private String status;
    private String description;
    private LocalDateTime timestamp;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
}
