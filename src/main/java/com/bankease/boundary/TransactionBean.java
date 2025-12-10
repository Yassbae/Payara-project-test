package com.bankease.boundary;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.dto.DepositRequest;
import ch.unil.doplab.bankease.dto.TransferRequest;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.TransactionService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;

@Named
@RequestScoped
public class TransactionBean implements Serializable {

    @Inject
    private TransactionService transactionService;

    @Inject
    private LoginBean loginBean;

    // --------- Champs du formulaire ---------
    // pour le dépôt
    private String accountNumber;

    // pour le virement
    private String sourceAccountNumber;
    private String destinationAccountNumber;

    private BigDecimal amount;
    private String description;

    // --------- Getters / setters requis par JSF ---------

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --------- Utilitaires internes ---------

    /**
     * ATTENTION : on doit renvoyer l'ID (clé primaire) du client,
     * pas son username.
     */
    private String getClientId() {
        Client c = loginBean.getLoggedClient();
        return (c != null) ? c.getId() : null;
    }

    // --------- Actions ---------

    public String makeDeposit() {
        try {
            String clientId = getClientId();

            DepositRequest req = new DepositRequest(
                    clientId,
                    accountNumber,
                    amount,
                    description
            );

            transactionService.deposit(req);

            addInfo("Dépôt réussi");
            return "dashboard?faces-redirect=true";

        } catch (ApiException ex) {
            addError(ex.getMessage());
            return null;
        }
    }

    public String makeTransfer() {
        try {
            String clientId = getClientId();

            TransferRequest req = new TransferRequest(
                    clientId,
                    sourceAccountNumber,
                    destinationAccountNumber,
                    amount,
                    description
            );

            transactionService.transfer(req);

            addInfo("Virement effectué");
            return "dashboard?faces-redirect=true";

        } catch (ApiException ex) {
            addError(ex.getMessage());
            return null;
        }
    }

    // --------- Messages ---------

    private void addInfo(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null)
        );
    }

    private void addError(String msg) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null)
        );
    }
}
