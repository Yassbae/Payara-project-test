package com.bankease.service;

import com.bankease.entity.Client;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ClientService {
    @PersistenceContext(unitName = "BankEasePU")
    private EntityManager em;

    public void save(Client c) { em.persist(c); }
    public Client find(String id) { return em.find(Client.class, id); }
}
