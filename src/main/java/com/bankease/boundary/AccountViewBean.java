package com.bankease.boundary;

import ch.unil.doplab.bankease.domain.Client;
import ch.unil.doplab.bankease.exception.ApiException;
import ch.unil.doplab.bankease.service.AccountService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class AccountViewBean implements Serializable {

    @Inject
    private AccountService accountService;

    @Inject
    private LoginBean loginBean;

    private List<Map<String, Object>> accounts;
    private BigDecimal totalBalance;

    public List<Map<String, Object>> getAccounts() {
        return accounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        Client logged = loginBean.getLoggedClient();
        if (logged == null) {
            // pas loggé → pas de crash, juste rien à afficher
            accounts = Collections.emptyList();
            totalBalance = BigDecimal.ZERO;
            return;
        }

        String clientId = logged.getId(); // ⚠️ IMPORTANT : on utilise l’ID, pas le username

        try {
            accounts = accountService.listAccounts(clientId);

            Map<String, Object> total = accountService.totalBalance(clientId);
            Object tb = total.get("totalBalance");
            if (tb instanceof BigDecimal b) {
                totalBalance = b;
            } else {
                totalBalance = BigDecimal.ZERO;
            }

        } catch (ApiException ex) {
            accounts = Collections.emptyList();
            totalBalance = BigDecimal.ZERO;

            ctx.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            ex.getMessage(), null));
        }
    }
}
