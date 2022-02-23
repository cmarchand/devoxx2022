package com.oxiane.formation.devoxx22.refacto.services.jdbc;

import com.oxiane.formation.devoxx22.refacto.model.SecteurGeographique;

import java.util.Calendar;

public interface DatabaseValuesExtractor {
    int getQuantiteDejaCommandeeCetteAnnee(Long clientId, Calendar date);
    SecteurGeographique getSecteurGeographiqueByDepartement(String departement);
}
