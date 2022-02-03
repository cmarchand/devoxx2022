package com.oxiane.formation.devoxx22.refacto.helpers;

import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;

public interface FacturePrinter {
    public String printFacture(Facture facture, Vistamboire vistamboire);
}
