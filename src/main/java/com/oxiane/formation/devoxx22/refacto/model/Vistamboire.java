package com.oxiane.formation.devoxx22.refacto.model;

import java.math.BigDecimal;

public class Vistamboire {
    private final BigDecimal prixUnitaireHT;
    private final BigDecimal tauxTVA;

    public Vistamboire(BigDecimal prixUnitaireHT, BigDecimal tauxTVA) {
        this.prixUnitaireHT = prixUnitaireHT;
        this.tauxTVA = tauxTVA;
    }

    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public BigDecimal getTauxTVA() { return tauxTVA; }
}
