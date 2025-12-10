package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.Employee;
import ch.unil.doplab.bankease.domain.Transaction;
import ch.unil.doplab.bankease.domain.TransactionStatus;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.EmployeeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Named("employeeService")
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @PersistenceContext(unitName = "bankeasePU")
    private EntityManager em;

    // ------------------------------------------------------------
    // APPROVE
    // ------------------------------------------------------------

    @Override
    public Map<String, Object> approve(String employeeId, String txId) {

        Employee emp = em.find(Employee.class, employeeId);
        if (emp == null) throw new ApiException(404, "Employee not found");

        Transaction tx = em.find(Transaction.class, txId);
        if (tx == null) throw new ApiException(404, "Transaction not found");

        emp.approve(tx);  // modifies the transaction domain object
        em.merge(tx);
        em.merge(emp);

        return txToMap(tx);
    }

    // ------------------------------------------------------------
    // REJECT
    // ------------------------------------------------------------

    @Override
    public Map<String, Object> reject(String employeeId, String txId) {

        Employee emp = em.find(Employee.class, employeeId);
        if (emp == null) throw new ApiException(404, "Employee not found");

        Transaction tx = em.find(Transaction.class, txId);
        if (tx == null) throw new ApiException(404, "Transaction not found");

        emp.reject(tx);
        em.merge(tx);
        em.merge(emp);

        return txToMap(tx);
    }

    // ------------------------------------------------------------
    // PENDING APPROVALS
    // ------------------------------------------------------------

    @Override
    public List<Map<String, Object>> pendingApprovals() {

        List<Transaction> list = em.createQuery(
                        "SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.timestamp DESC",
                        Transaction.class
                )
                .setParameter("status", TransactionStatus.PENDING_APPROVAL)
                .getResultList();

        return list.stream()
                .map(this::txToMap)
                .toList();
    }

    // ------------------------------------------------------------
    // HELPER
    // ------------------------------------------------------------

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
