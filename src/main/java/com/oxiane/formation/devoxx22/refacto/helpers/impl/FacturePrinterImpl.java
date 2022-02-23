package com.oxiane.formation.devoxx22.refacto.helpers.impl;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.SecteurGeographique;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FacturePrinterImpl implements FacturePrinter {
    @Autowired
    private DatabaseValuesExtractor databaseValuesExtractor;

    private static final String PRINT_TEMPLATE_HEADBODY = """
            Facture %1$d / %2$s

            %3$s %4$s
            %5$s
            %6$s %7$s
            %8$s

            ____________________________________________________________________
            | Article                    | Prix Unitaire | Quantité | Taux TVA |
            |----------------------------|---------------|----------|----------|
            | %15$-26s | %9$13.2f | %14$8d | %10$8.2f |
            ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
            """;
    private static final String PRINT_TEMPLATE_DISCOUNT = """
                                                Remise            :  %1$9.2f %%
            """;
    private static final String PRINT_TEMPLATE_FOOT = """
                                                Montant Hors Taxe : %11$10.2f €
                                                Montant Total TVA : %12$10.2f €
                                                Montant Total TTC : %13$10.2f €
            """;
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    private static final Logger LOGGER = LoggerFactory.getLogger(FacturePrinterImpl.class);
    private static final String VISTAMBOIRE_INOX = "Vistamboire inoxydable";
    private static final String VISTAMBOIRE_NICKELE = "Vistamboire coins nickelés";

    public String printFacture(Facture facture, Vistamboire vistamboire) {
        StringBuilder sb = new StringBuilder();
        sb.append( String.format(PRINT_TEMPLATE_HEADBODY,
                facture.getId(),                                            //  1
                DATE_FORMATTER.format(facture.getDate().getTime()),         //  2
                facture.getClient().getPrenom(),                            //  3
                facture.getClient().getNom(),                               //  4
                facture.getClient().getAdresse().lignes(),                  //  5
                facture.getClient().getAdresse().getCodepostal(),           //  6
                facture.getClient().getAdresse().getVille(),                //  7
                facture.getClient().getAdresse().getPays(),                 //  8
                vistamboire.getPrixUnitaireHT(),                            //  9
                vistamboire.getTauxTVA(),                                   // 10
                facture.getTotalHT(),                                       // 11
                facture.getTotalTVA(),                                      // 12
                facture.getTotalTTC(),                                      // 13
                facture.getQte(),                                           // 14
                getLibelleVistamboire(facture.getClient().getAdresse()))    // 15
        );
        if(!BigDecimal.ZERO.equals(facture.getRemiseClient())) {
            sb.append(String.format(PRINT_TEMPLATE_DISCOUNT, facture.getRemiseClient().multiply(BigDecimal.valueOf(100))));
        }
        sb.append( String.format(PRINT_TEMPLATE_FOOT,
                facture.getId(),
                DATE_FORMATTER.format(facture.getDate().getTime()),
                facture.getClient().getPrenom(),
                facture.getClient().getNom(),
                facture.getClient().getAdresse().lignes(),
                facture.getClient().getAdresse().getCodepostal(),
                facture.getClient().getAdresse().getVille(),
                facture.getClient().getAdresse().getPays(),
                vistamboire.getPrixUnitaireHT(),
                vistamboire.getTauxTVA(),
                facture.getTotalHT(),
                facture.getTotalTVA(),
                facture.getTotalTTC(),
                facture.getQte())
        );
        return sb.toString();
    }

    private String getLibelleVistamboire(Adresse adresse) {
        SecteurGeographique secteurGeographique = databaseValuesExtractor.getSecteurGeographiqueByDepartement(adresse.getDepartement());
        LOGGER.debug("secteurGeographique: {}", secteurGeographique);
        if(SecteurGeographique.NOM_MARITIME.equals(secteurGeographique.getNom())) {
            return VISTAMBOIRE_INOX;
        } else {
            return VISTAMBOIRE_NICKELE;
        }
    }
}
