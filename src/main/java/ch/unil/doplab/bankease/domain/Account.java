package ch.unil.doplab.bankease.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_number", nullable = false, updatable = false, length = 64)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AccountType type;

    @Column(name = "opening_date", nullable = false)
    private LocalDateTime openingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client owner;

    // 🔥 IMPORTANT : mappage CORRECT des transactions
    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Transaction> incomingTransactions = new ArrayList<>();

    protected Account() {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = BigDecimal.ZERO;
        this.openingDate = LocalDateTime.now();
    }

    public Account(Client owner, AccountType type) {
        this();
        this.owner = Objects.requireNonNull(owner, "owner");
        this.type = Objects.requireNonNull(type, "type");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountType getType() {
        return type;
    }

    public LocalDateTime getOpeningDate() {
        return openingDate;
    }

    public Client getOwner() {
        return owner;
    }

    public List<Transaction> getOutgoingTransactions() {
        return List.copyOf(outgoingTransactions);
    }

    public List<Transaction> getIncomingTransactions() {
        return List.copyOf(incomingTransactions);
    }

    // ========== Métier ==========

    public void receiveDeposit(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        add(amount);
    }

    public void processWithdrawal(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount");
        if (!subtract(amount)) {
            throw new IllegalStateException("Insufficient funds");
        }
    }

    public void attachTransaction(Transaction tx) {
        if (tx == null) return;

        if (tx.getSource() == this && !outgoingTransactions.contains(tx)) {
            outgoingTransactions.add(tx);
        }

        if (tx.getDestination() == this && !incomingTransactions.contains(tx)) {
            incomingTransactions.add(tx);
        }
    }

    // Helpers

    private void add(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    private boolean subtract(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) return false;
        this.balance = this.balance.subtract(amount);
        return true;
    }
}
