package com.oxiane.formation.devoxx22.refacto.config;

import com.oxiane.formation.devoxx22.refacto.model.SecteurGeographique;

import java.math.BigDecimal;

public interface TestData {
    public static final SecteurGeographique SECTEUR_GEO_MARITIME = new SecteurGeographique(SecteurGeographique.NOM_MARITIME, BigDecimal.valueOf(1.15));
    public static final SecteurGeographique SECTEUR_GEO_TERRE = new SecteurGeographique(SecteurGeographique.NOM_AUTRE, BigDecimal.ONE);
}
