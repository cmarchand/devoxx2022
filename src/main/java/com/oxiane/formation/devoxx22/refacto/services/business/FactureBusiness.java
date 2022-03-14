package com.oxiane.formation.devoxx22.refacto.services.business;

import com.oxiane.formation.devoxx22.refacto.model.Facture;

public interface FactureBusiness {
    Facture createAndSaveFacture(Long clientId, int qte);
    Facture getFacture(Long id);
    public Iterable<Facture> getFactures();
    public String printFacture(Long id);
}
