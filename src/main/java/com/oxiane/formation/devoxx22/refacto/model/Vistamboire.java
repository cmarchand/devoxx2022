package com.oxiane.formation.devoxx22.refacto.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Calendar;

@Entity
public class Vistamboire {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Calendar validSince;
    private Calendar validUntil;
    private BigDecimal prixUnitaireHT;
    private BigDecimal tauxTVA;

    public Vistamboire() {}

    public Vistamboire(BigDecimal prixUnitaireHT, BigDecimal tauxTVA, Calendar validSince, Calendar validUntil) {
        this();
        this.prixUnitaireHT = prixUnitaireHT;
        this.tauxTVA = tauxTVA;
        this.validSince = validSince;
        this.validUntil = validUntil;
    }

    public BigDecimal getPrixUnitaireHT() { return prixUnitaireHT; }
    public BigDecimal getTauxTVA() { return tauxTVA; }

    public Long getId() {
        return id;
    }

    public Calendar getValidSince() {
        return validSince;
    }

    public Calendar getValidUntil() {
        return validUntil;
    }
}
