package ch.unil.doplab.bankease.config;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.annotation.PostConstruct;

import java.util.logging.Logger;

@Singleton
@Startup
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());

    @PostConstruct
    public void init() {
        // ⚠️ IMPORTANT : on ne fait plus rien ici.
        // On laisse JPA créer le schéma, mais on ne seed plus la DB au démarrage.
        LOGGER.info("DatabaseInitializer: disabled (no initial data seeding).");
    }
}
