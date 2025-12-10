package com.bankease.boundary;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.dto.CreateAccountRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.AccountService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@RequestScoped
public class CreateAccountBean implements Serializable {

    private String type;   // CURRENT / SAVINGS / BUSINESS

    @Inject
    private AccountService accountService;

    @Inject
    private LoginBean loginBean;

    // ================== Getter / setter ==================

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // ================== Action ==================

    public String open() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            // 1) Récupérer le client connecté
            Client logged = loginBean.getLoggedClient();
            if (logged == null || logged.getId() == null) {
                throw new ApiException(401, "Aucun client connecté");
            }

            // 2) Construire la requête pour le service
            CreateAccountRequest req = new CreateAccountRequest(type);

            // 3) Appeler le service JPA
            accountService.openAccount(logged.getId(), req);

            // 4) Message de succès
            ctx.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Compte créé avec succès.",
                    null));

            // Retour au dashboard
            return "dashboard?faces-redirect=true";

        } catch (ApiException e) {
            // On affiche EXACTEMENT le message du service (Client not found, etc.)
            ctx.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    e.getMessage(),
                    null));
            return null;

        } catch (Exception e) {
            // filet de sécurité pour toute autre erreur
            ctx.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erreur création compte : " + e.getMessage(),
                    null));
            return null;
        }
    }
}
