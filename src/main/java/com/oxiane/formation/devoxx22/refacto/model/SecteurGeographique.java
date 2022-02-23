package com.oxiane.formation.devoxx22.refacto.model;

import java.math.BigDecimal;

public class SecteurGeographique {
    public static final String NOM_AUTRE = "AUTRE";
    public static final String NOM_MARITIME = "MARITIME";
    private Long id;
    private String nom;

    private BigDecimal coefficientMultiplicateur;

    public SecteurGeographique(String nom, BigDecimal coefficientMultiplicateur) {
        this.nom = nom;
        this.coefficientMultiplicateur = coefficientMultiplicateur;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public BigDecimal getCoefficientMultiplicateur() {
        return coefficientMultiplicateur;
    }

    @Override
    public String toString() {
        return "SecteurGeographique{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", coefficientMultiplicateur=" + coefficientMultiplicateur +
                '}';
    }
}
