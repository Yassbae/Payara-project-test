package ch.unil.doplab.bankease.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client extends User {

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    protected Client() {
        // pour JPA
    }

    public Client(String username, String password, String firstName, String lastName,
                  String email, String phoneNumber) {
        super(username, password, firstName, lastName, email, phoneNumber);
    }

    // ================== Relations ==================

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    // ================== API utilisée partout ==================

    /** Création d'un compte pour ce client (utilisé par InMemoryStore, AccountServiceImpl, DataLoader). */
    public Account openAccount(AccountType type) {
        Objects.requireNonNull(type, "Account type cannot be null");
        Account account = new Account(this, type);
        accounts.add(account);
        return account;
    }

    /** Dépôt sur un compte du client. */
    public void deposit(Account destination, BigDecimal amount, String description) {
        ensureOwns(destination);
        Objects.requireNonNull(amount, "amount");

        destination.receiveDeposit(amount);

        Transaction tx = Transaction.createDeposit(destination, amount, description);
        transactions.add(tx);
        destination.attachTransaction(tx);
    }

    /** Retrait depuis un compte du client. */
    public void withdraw(Account source, BigDecimal amount, String description) {
        ensureOwns(source);
        Objects.requireNonNull(amount, "amount");

        source.processWithdrawal(amount);

        Transaction tx = Transaction.createWithdrawal(source, amount, description);
        transactions.add(tx);
        source.attachTransaction(tx);
    }

    /**
     * Virement entre deux comptes.
     * Retourne la Transaction créée (utilisé par TransactionServiceImpl).
     */
    public Transaction transfer(Account source, Account destination, BigDecimal amount, String description) {
        ensureOwns(source);
        Objects.requireNonNull(destination, "destination");
        Objects.requireNonNull(amount, "amount");

        if (source == destination) {
            throw new IllegalArgumentException("Source and destination accounts must differ.");
        }

        // Mouvement d'argent
        source.processWithdrawal(amount);
        destination.receiveDeposit(amount);

        Transaction tx = Transaction.createTransfer(
                source,
                destination,
                amount,
                description,
                TransactionStatus.COMPLETED
        );
        transactions.add(tx);
        source.attachTransaction(tx);
        destination.attachTransaction(tx);

        return tx;
    }

    // ================== Utilitaires ==================

    public BigDecimal getTotalBalance() {
        return accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> viewTransactionHistory() {
        return List.copyOf(transactions);
    }

    public void requestCancellation(Transaction tx) {
        if (tx.getStatus() != TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Only completed transactions can be canceled directly.");
        }
        tx.cancel();
    }

    private void ensureOwns(Account account) {
        if (account == null || !accounts.contains(account)) {
            throw new IllegalArgumentException("Client does not own the specified account.");
        }
    }
}