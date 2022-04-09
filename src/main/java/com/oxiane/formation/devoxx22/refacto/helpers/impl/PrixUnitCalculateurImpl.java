package com.oxiane.formation.devoxx22.refacto.helpers.impl;

import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.SecteurGeographique;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class PrixUnitCalculateurImpl implements PrixUnitCalculateur {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrixUnitCalculateurImpl.class);
    @Autowired
    private DatabaseValuesExtractor databaseValuesExtractor;

    @Override
    public BigDecimal calculatePrixUnit(
            Vistamboire vistamboire,
            Client client) {
        BigDecimal prixUnitaireTypeClient = ClientType.of(client).calculatePrixUnit(vistamboire);
        SecteurGeographique secteurGeographique = databaseValuesExtractor.getSecteurGeographiqueByDepartement(client.getAdresse().getDepartement());
        if(secteurGeographique==null) {
            return prixUnitaireTypeClient;
        } else {
            return prixUnitaireTypeClient.multiply(secteurGeographique.getCoefficientMultiplicateur());
        }
    }

    @Override
    public BigDecimal calculateRemiseClient(
            Client client,
            int qteDejaAchetee,
            int quantite) {
        SecteurGeographique secteurGeographique = databaseValuesExtractor.getSecteurGeographiqueByDepartement(client.getAdresse().getDepartement());
        return ClientType.of(client).calculateRemise(secteurGeographique, qteDejaAchetee+quantite);
        if(Client.TYPE_PARTICULIER.equals(client.getType())) {
            return BigDecimal.ZERO;
        } else {
            return RemiseSecteurGeo.of(secteurGeographique).calculateRemise(qteDejaAchetee + quantite);
        }
    }

    private enum ClientType {
        PARTICULIER(Client.TYPE_PARTICULIER, BigDecimal.ONE, remiseCalculator),
        PROFESSIONNEL(Client.TYPE_PROFESSIONNEL, BigDecimal.valueOf(0.7), remiseCalculator),
        UNKNOWN("", BigDecimal.ONE, remiseCalculator)
        ;

        private final String code;
        private final BigDecimal coefMultiplicateur;
        private final BiFunction<SecteurGeographique, Integer, BigDecimal> remiseCalculator;

        ClientType(String code, BigDecimal coefMultiplicateur, BiFunction<SecteurGeographique, Integer, BigDecimal> remiseCalculator) {
            this.code = code;
            this.coefMultiplicateur = coefMultiplicateur;
            this.remiseCalculator = remiseCalculator;
        }

        public static ClientType of(Client client) {
            return Arrays.stream(values())
                    .filter(clientType -> clientType.code.equals(client.getType()))
                    .findFirst()
                    .orElse(UNKNOWN);
        }

        public BigDecimal calculatePrixUnit(Vistamboire vistamboire) {
            return vistamboire.getPrixUnitaireHT().multiply(coefMultiplicateur);
        }

        public BigDecimal calculateRemise(SecteurGeographique secteurGeographique, int qte) {
            return remiseCalculator.apply(secteurGeographique, qte);
        }
    }

    private enum RemiseSecteurGeo {
        AUTRE(
                SecteurGeographique.NOM_AUTRE,
                Arrays.asList(
                        new Seuil(50, BigDecimal.valueOf(0.2)),
                        new Seuil(20, BigDecimal.valueOf(0.15)),
                        new Seuil(10, BigDecimal.valueOf(0.1))
                )
        ),
        MARITIME(
                SecteurGeographique.NOM_MARITIME,
                Arrays.asList(
                        new Seuil(40, BigDecimal.valueOf(0.2)),
                        new Seuil(20, BigDecimal.valueOf(0.17)),
                        new Seuil(15, BigDecimal.valueOf(0.1))
                )
        ),
        UNKOWN(
                "",
                Arrays.asList(
                        new Seuil(50, BigDecimal.valueOf(0.2)),
                        new Seuil(20, BigDecimal.valueOf(0.15)),
                        new Seuil(10, BigDecimal.valueOf(0.1))
                )
        )
        ;
        record Seuil(int qte, BigDecimal remise) {};
        private final String nom;
        private final List<Seuil> seuils;

        RemiseSecteurGeo(String nom, List<Seuil> seuils) {
            this.nom = nom;
            this.seuils = seuils;
        }

        public static RemiseSecteurGeo of(SecteurGeographique secteurGeographique) {
            return Arrays.stream(values())
                    .filter(remiseSecteurGeo -> remiseSecteurGeo.nom.equals(secteurGeographique.getNom()))
                    .findFirst()
                    .orElse(UNKOWN);
        }

        public BigDecimal calculateRemise(int qteFinale) {
            return seuils.stream()
                    .filter(seuil -> qteFinale > seuil.qte())
                    .map(seuil -> seuil.remise())
                    .findFirst()
                    .orElse(BigDecimal.ZERO);
        }
    }
}
