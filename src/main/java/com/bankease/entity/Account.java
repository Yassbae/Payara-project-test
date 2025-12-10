package com.bankease.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String iban;
    private String type;
    private double balance;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "accountFrom")
    private List<Transaction> transactionsFrom;

    @OneToMany(mappedBy = "accountTo")
    private List<Transaction> transactionsTo;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
