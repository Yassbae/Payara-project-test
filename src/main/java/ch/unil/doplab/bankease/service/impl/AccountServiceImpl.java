package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.*;
import ch.unil.doplab.bankease.dto.CreateAccountRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Named("accountService")
@Transactional
public class AccountServiceImpl implements AccountService {

    @PersistenceContext(unitName = "bankeasePU")
    private EntityManager em;

    @Override
    public Map<String, Object> openAccount(String clientId, CreateAccountRequest req) {

        Client client = em.find(Client.class, clientId);
        if (client == null) throw new ApiException(404, "Client not found");

        AccountType type;
        try {
            type = AccountType.valueOf(req.type());
        } catch (Exception e) {
            throw new ApiException(400, "Invalid account type");
        }

        Account acc = client.openAccount(type);

        // Persist new account
        em.persist(acc);
        em.merge(client);

        Map<String, Object> map = new HashMap<>();
        map.put("clientId", client.getId());
        map.put("accountNumber", acc.getAccountNumber());
        map.put("type", acc.getType().name());
        map.put("balance", acc.getBalance());
        map.put("openingDate", acc.getOpeningDate());

        return map;
    }

    @Override
    public List<Map<String, Object>> listAccounts(String clientId) {

        Client client = em.find(Client.class, clientId);
        if (client == null) throw new ApiException(404, "Client not found");

        return client.getAccounts().stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("accountNumber", a.getAccountNumber());
            m.put("type", a.getType().name());
            m.put("balance", a.getBalance());
            m.put("openingDate", a.getOpeningDate());
            return m;
        }).toList();
    }

    @Override
    public Map<String, Object> totalBalance(String clientId) {

        Client client = em.find(Client.class, clientId);
        if (client == null) throw new ApiException(404, "Client not found");

        Map<String, Object> map = new HashMap<>();
        map.put("clientId", client.getId());
        map.put("totalBalance", client.getTotalBalance());
        return map;
    }
}
