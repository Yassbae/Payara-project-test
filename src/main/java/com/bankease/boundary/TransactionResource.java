package com.bankease.boundary;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Ancienne API REST basée sur InMemoryStore.
 * Désactivée après la migration vers JPA + MySQL.
 */
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {
    // Ressource REST désactivée.
}
