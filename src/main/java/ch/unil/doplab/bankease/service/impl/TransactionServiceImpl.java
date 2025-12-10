package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.Account;
import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.domain.Transaction;
import ch.unil.doplab.bankease.domain.TransactionStatus;
import ch.unil.doplab.bankease.dto.DepositRequest;
import ch.unil.doplab.bankease.dto.TransferRequest;
import ch.unil.doplab.bankease.dto.WithdrawRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.TransactionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @PersistenceContext(unitName = "bankeasePU")
    private EntityManager em;

    // ===============================
    // DEPOSIT
    // ===============================

    @Override
    public Map<String, Object> deposit(DepositRequest req) {

        Client c = em.find(Client.class, req.clientId());
        if (c == null) {
            throw new ApiException(404, "Client not found");
        }

        Account acc = em.find(Account.class, req.accountNumber());
        if (acc == null || acc.getOwner() == null || !acc.getOwner().equals(c)) {
            throw new ApiException(404, "Account not found for this client");
        }

        acc.receiveDeposit(req.amount());
        Transaction tx = Transaction.createDeposit(acc, req.amount(), req.description());
        tx.setClient(c);
        acc.attachTransaction(tx);

        em.persist(tx);
        em.merge(acc);
        em.merge(c);

        return txToMap(tx);
    }

    // ===============================
    // WITHDRAW
    // ===============================

    @Override
    public Map<String, Object> withdraw(WithdrawRequest req) {

        Client c = em.find(Client.class, req.clientId());
        if (c == null) {
            throw new ApiException(404, "Client not found");
        }

        Account acc = em.find(Account.class, req.accountNumber());
        if (acc == null || acc.getOwner() == null || !acc.getOwner().equals(c)) {
            throw new ApiException(404, "Account not found for this client");
        }

        acc.processWithdrawal(req.amount());
        Transaction tx = Transaction.createWithdrawal(acc, req.amount(), req.description());
        tx.setClient(c);
        acc.attachTransaction(tx);

        em.persist(tx);
        em.merge(acc);
        em.merge(c);

        return txToMap(tx);
    }

    // ===============================
    // TRANSFER
    // ===============================

    @Override
    public Map<String, Object> transfer(TransferRequest req) {

        Client c = em.find(Client.class, req.clientId());
        if (c == null) {
            throw new ApiException(404, "Client not found");
        }

        Account source = em.find(Account.class, req.sourceAccountNumber());
        Account dest   = em.find(Account.class, req.destinationAccountNumber());

        if (source == null || dest == null) {
            throw new ApiException(404, "Source or destination account not found");
        }

        // Vérifier que le compte source appartient au client
        if (source.getOwner() == null || !source.getOwner().equals(c)) {
            throw new ApiException(403, "Source account does not belong to this client");
        }

        source.processWithdrawal(req.amount());
        dest.receiveDeposit(req.amount());

        Transaction tx = Transaction.createTransfer(
                source,
                dest,
                req.amount(),
                req.description(),
                TransactionStatus.COMPLETED
        );
        tx.setClient(c);
        source.attachTransaction(tx);
        dest.attachTransaction(tx);

        em.persist(tx);
        em.merge(source);
        em.merge(dest);
        em.merge(c);

        return txToMap(tx);
    }

    // ===============================
    // CANCEL
    // ===============================

    @Override
    public Map<String, Object> cancel(String txId) {

        Transaction tx = em.find(Transaction.class, txId);
        if (tx == null) {
            throw new ApiException(404, "Transaction not found");
        }

        if (tx.getStatus() != TransactionStatus.COMPLETED) {
            throw new ApiException(400, "Only completed transactions can be canceled");
        }

        tx.cancel();
        em.merge(tx);

        return txToMap(tx);
    }

    // ===============================
    // HISTORY
    // ===============================

    @Override
    public List<Map<String, Object>> historyByClient(String clientId) {

        List<Transaction> list = em.createQuery(
                        "SELECT t FROM Transaction t " +
                                "WHERE t.client.id = :cid " +
                                "ORDER BY t.timestamp DESC",
                        Transaction.class)
                .setParameter("cid", clientId)
                .getResultList();

        return list.stream()
                .map(this::txToMap)
                .collect(Collectors.toList());
    }

    // ===============================
    // HELPER
    // ===============================

    private Map<String, Object> txToMap(Transaction t) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", t.getId());
        map.put("type", t.getType().name());
        map.put("amount", t.getAmount());
        map.put("description", t.getDescription());
        map.put("timestamp", t.getTimestamp());
        map.put("status", t.getStatus().name());
        map.put("source", t.getSource() == null ? "-" : t.getSource().getAccountNumber());
        map.put("destination", t.getDestination() == null ? "-" : t.getDestination().getAccountNumber());
        return map;
    }
}
