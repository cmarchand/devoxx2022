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

public class PrixUnitCalculateurImpl implements PrixUnitCalculateur {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrixUnitCalculateurImpl.class);
    @Autowired
    private DatabaseValuesExtractor databaseValuesExtractor;

    @Override
    public BigDecimal calculatePrixUnit(
            Vistamboire vistamboire,
            Client client) {
        // étape 1 : en fonction du type de client
        BigDecimal prixUnitaireTypeClient = ClientType.of(client).calculatePrixUnit(vistamboire);
        if (Client.TYPE_PARTICULIER.equals(client.getType())) {
            prixUnitaireTypeClient = vistamboire.getPrixUnitaireHT();
        } else if (Client.TYPE_PROFESSIONNEL.equals(client.getType())) {
            prixUnitaireTypeClient = vistamboire.getPrixUnitaireHT().multiply(new BigDecimal(0.7));
        } else {
            LOGGER.warn("Type de client inconnu : {}", client.getType());
            prixUnitaireTypeClient = vistamboire.getPrixUnitaireHT();
        }
        // étape 2 : en fonction du secteur géographique
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
        if(Client.TYPE_PARTICULIER.equals(client.getType())) {
            return BigDecimal.ZERO;
        } else {
            SecteurGeographique secteurGeographique = databaseValuesExtractor.getSecteurGeographiqueByDepartement(client.getAdresse().getDepartement());
            int qteFinale = qteDejaAchetee + quantite;
            BigDecimal remise;
            if(secteurGeographique==null || SecteurGeographique.NOM_AUTRE.equals(secteurGeographique.getNom())) {
                if (qteFinale > 50) remise = BigDecimal.valueOf(0.2);
                else if (qteFinale > 20) remise = BigDecimal.valueOf(0.15);
                else if (qteFinale > 10) remise = BigDecimal.valueOf(0.1);
                else remise = BigDecimal.ZERO;
            } else {
                if (qteFinale > 40) remise = BigDecimal.valueOf(0.2);
                else if (qteFinale > 25) remise = BigDecimal.valueOf(0.17);
                else if (qteFinale > 15) remise = BigDecimal.valueOf(0.1);
                else remise = BigDecimal.ZERO;
            }
            return remise;
        }
    }

    private enum ClientType {
        ;

        private final String code;

        ClientType(String code) {
            this.code = code;
        }

        public static ClientType of(Client client) {
            return Arrays.stream(values())
                    .filter(clientType -> clientType.code.equals(client.getType()))
                    .findFirst()
                    .orElseThrow();
        }

        public BigDecimal calculatePrixUnit(Vistamboire vistamboire) {
            return vistamboire.getPrixUnitaireHT().multiply(coefMultiplicateur);
        }
    }
}
