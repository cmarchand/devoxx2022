package com.oxiane.formation.devoxx22.refacto.helpers.impl;

import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;

import java.math.BigDecimal;

public class PrixUnitCalculateurImpl implements PrixUnitCalculateur {
    @Override
    public BigDecimal calculatePrixUnit(
            Vistamboire vistamboire,
            Client client) {
        if (Client.TYPE_PARTICULIER.equals(client.getType())) {
            return vistamboire.getPrixUnitaireHT();
        } else if (Client.TYPE_PROFESSIONNEL.equals(client.getType())) {
            return vistamboire.getPrixUnitaireHT().multiply(new BigDecimal(0.7));
        }
        return null;
    }

    @Override
    public BigDecimal calculateRemiseClient(
            Client client,
            int qteDejaAchetee,
            int quantite) {
        if(Client.TYPE_PARTICULIER.equals(client.getType())) return BigDecimal.ZERO;
        int qteFinale = qteDejaAchetee + quantite;
        BigDecimal remise;
        if (qteFinale > 50) remise = new BigDecimal(0.2);
        else if (qteFinale > 20) remise = new BigDecimal(0.15);
        else if (qteFinale > 10) remise = new BigDecimal(0.1);
        else remise = BigDecimal.ZERO;
        return remise;
    }
}
