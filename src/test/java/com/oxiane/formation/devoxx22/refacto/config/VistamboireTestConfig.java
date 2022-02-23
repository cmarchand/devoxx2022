package com.oxiane.formation.devoxx22.refacto.config;

import com.oxiane.formation.devoxx22.refacto.model.SecteurGeographique;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Calendar;

@Configuration
public class VistamboireTestConfig extends VistamboireConfig {
    @Override
    public DatabaseValuesExtractor databaseValuesExtractor() {
        return new DatabaseValuesExtractor() {
            private SecteurGeographique SECTEUR_GEO_MARITIME = new SecteurGeographique(SecteurGeographique.NOM_MARITIME, BigDecimal.valueOf(1.15));
            private static final SecteurGeographique SECTEUR_GEO_TERRE = new SecteurGeographique(SecteurGeographique.NOM_AUTRE, BigDecimal.ONE);

            @Override
            public int getQuantiteDejaCommandeeCetteAnnee(Long clientId, Calendar date) {
                return 0;
            }

            @Override
            public SecteurGeographique getSecteurGeographiqueByDepartement(String departement) {
                if("75".equals(departement)) {
                    return SECTEUR_GEO_TERRE;
                } else if("76".equals(departement)) {
                    return SECTEUR_GEO_MARITIME;
                }
                return null;
            }
        };
    }
}
