package ch.unil.doplab.bankease.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account")
    private Account source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account")
    private Account destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // ===== Constructeurs =====

    protected Transaction() {
        // pour JPA
    }

    public Transaction(
            TransactionType type,
            BigDecimal amount,
            String description,
            Account source,
            Account destination
    ) {
        this.id = UUID.randomUUID().toString();
        this.type = Objects.requireNonNull(type, "type");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.description = description;
        this.source = source;
        this.destination = destination;
        this.status = TransactionStatus.COMPLETED;
        this.timestamp = LocalDateTime.now();
    }

    // ===== Fabriques statiques (utilisées dans Client.java) =====

    public static Transaction createDeposit(Account destination, BigDecimal amount, String description) {
        Objects.requireNonNull(destination, "destination");
        return new Transaction(TransactionType.DEPOSIT, amount, description, null, destination);
    }

    public static Transaction createWithdrawal(Account source, BigDecimal amount, String description) {
        Objects.requireNonNull(source, "source");
        return new Transaction(TransactionType.WITHDRAWAL, amount, description, source, null);
    }

    public static Transaction createTransfer(
            Account source,
            Account destination,
            BigDecimal amount,
            String description,
            TransactionStatus status
    ) {
        Transaction tx = new Transaction(TransactionType.TRANSFER, amount, description, source, destination);
        tx.status = Objects.requireNonNull(status, "status");
        return tx;
    }

    // ===== Logique métier =====

    public void cancel() {
        this.status = TransactionStatus.CANCELED;
    }

    public void markApproved() {
        this.status = TransactionStatus.APPROVED;
    }

    public void markRejected() {
        this.status = TransactionStatus.REJECTED;
    }

    // ===== Getters / setters =====

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Account getSource() {
        return source;
    }

    public Account getDestination() {
        return destination;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
