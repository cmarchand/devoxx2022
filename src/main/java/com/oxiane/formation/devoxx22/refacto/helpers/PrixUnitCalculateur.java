package com.oxiane.formation.devoxx22.refacto.helpers;

import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;

import java.math.BigDecimal;

public interface PrixUnitCalculateur {
    /**
     * Renvoie le prix unitaire à appliquer pour la vente de ce vistamboire
     * à ce client dans cette quantité
     * @param vistamboire Le vistamboire vendu
     * @param client Le client acheteur
     * @return Le prix Unitaire à appliquer
     */
    BigDecimal calculatePrixUnit(Vistamboire vistamboire, Client client);

    /**
     * Renvoie le pourcentage de remise à appliquer à la facture pour un client professionnel, qui a déjà
     * acheté un nombre connu de vistamboires dans lannée, et qui en rachète.
     *
     * @param client
     * @param qteDejaAcheter
     * @param qteAchetee
     * @return Le pourcentage de remise à appliquer. 0.3 correspond à 30% de remise.
     */
    BigDecimal calculateRemiseClient(Client client, int qteDejaAcheter, int qteAchetee);
}
