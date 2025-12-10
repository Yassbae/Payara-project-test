package ch.unil.doplab.bankease.config;

// ⚠️ DataLoader désactivé car on utilise MySQL + JPA maintenant.
//    On garde le fichier pour référence, mais il ne fait plus rien.

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DataLoader {

    // Désactivé volontairement
    // @PostConstruct
    // public void init() {
    //     // plus utilisé
    // }
}
