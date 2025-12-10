package ch.unil.doplab.bankease.service.impl;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.ClientService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Named("clientService")
@Transactional
public class ClientServiceImpl implements ClientService {

    @PersistenceContext(unitName = "bankeasePU")
    private EntityManager em;

    @Override
    public Client register(String username,
                           String password,
                           String firstName,
                           String lastName,
                           String email,
                           String phoneNumber) {

        if (username == null || username.isBlank())
            throw new ApiException(400, "Username is required");

        if (password == null || password.isBlank())
            throw new ApiException(400, "Password is required");

        // Vérifier si l'username existe déjà
        var existing = em.createQuery(
                        "SELECT c FROM Client c WHERE c.username = :u", Client.class)
                .setParameter("u", username)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null)
            throw new ApiException(400, "Username already exists");

        Client client = new Client(
                username,
                password,
                firstName,
                lastName,
                email,
                phoneNumber
        );

        em.persist(client);
        return client;
    }

    @Override
    public Client findByUsername(String username) {
        if (username == null) return null;

        return em.createQuery(
                        "SELECT c FROM Client c WHERE c.username = :u", Client.class)
                .setParameter("u", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Client validateLogin(String username, String password) {
        Client c = findByUsername(username);
        if (c == null) return null;
        if (!c.getPassword().equals(password)) return null;
        return c; // ici : c.getId() n'est plus null !
    }
}
