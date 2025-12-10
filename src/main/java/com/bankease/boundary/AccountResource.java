package com.bankease.boundary;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Ancienne API REST basée sur InMemoryStore.
 * Désactivée après la migration vers JPA + MySQL.
 */
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    // Ressource REST désactivée.
}
