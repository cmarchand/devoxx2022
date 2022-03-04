package com.oxiane.formation.devoxx22.refacto.helpers;

import com.oxiane.formation.devoxx22.refacto.helpers.impl.FacturePrinterImpl;
import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import com.oxiane.formation.devoxx22.refacto.model.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.oxiane.formation.devoxx22.refacto.config.TestData.SECTEUR_GEO_MARITIME;
import static com.oxiane.formation.devoxx22.refacto.config.TestData.SECTEUR_GEO_TERRE;

@SpringJUnitConfig
public class FacturePrinterTest {
    // Warning : Monthes are 0-based !!!
    private static final Calendar FIXED_DATE= new GregorianCalendar(2022, 0, 31);
    private static final Calendar LOWER_BOUND = new GregorianCalendar(2022, 0, 1);
    private static final Calendar UPPER_BOUND = new GregorianCalendar(2022, 5, 1);

    @MockBean
    private DatabaseValuesExtractor databaseValuesExtractor;

    @BeforeEach
    public void beforeEach() {
        Mockito.when(
                databaseValuesExtractor.getQuantiteDejaCommandeeCetteAnnee(
                        Mockito.anyLong(),
                        Mockito.any())
        ).thenReturn(0);
        Mockito.when(
                databaseValuesExtractor.getSecteurGeographiqueByDepartement("75")
        ).thenReturn(SECTEUR_GEO_TERRE);
        Mockito.when(
                databaseValuesExtractor.getSecteurGeographiqueByDepartement("76")
        ).thenReturn(SECTEUR_GEO_MARITIME);
    }
    @Configuration
    static class Config {
        @Bean
        public FacturePrinter printer() { return new FacturePrinterImpl(); }
    }
    @Autowired
    private FacturePrinter printer;

    @Test
    public void given_facture_no_promotion_with_qte_1_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = getAdresseSecteurGeoAutre();
        Client client = getClientMichelChombier(adresse);
        Facture facture = new Facture(client, FIXED_DATE);
        Vistamboire vistamboire = getVistamboire();
        facture.calculate(vistamboire);
        String expected = """
                Facture null / 31-01-2022
                            
                Michel Chombier
                98 Boulevard Soult
                75012 Paris
                France
                    
                ____________________________________________________________________
                | Article                    | Prix Unitaire | Quantité | Taux TVA |
                |----------------------------|---------------|----------|----------|
                | Vistamboire coins nickelés |         10,00 |        1 |     0,20 |
                ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
                                                    Montant Hors Taxe :      10,00 €
                                                    Montant Total TVA :       2,00 €
                                                    Montant Total TTC :      12,00 €
                """;
        // Then
        verifyFactureCorrectlyPrinted(facture, vistamboire, expected);
    }
    @Test
    public void given_facture_no_promotion_with_qte_2_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = getAdresseSecteurGeoAutre();
        Client client = getClientMichelChombier(adresse);
        Facture facture = new Facture(client, FIXED_DATE, 2);
        Vistamboire vistamboire = getVistamboire();
        facture.calculate(vistamboire);
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            98 Boulevard Soult
            75012 Paris
            France
    
            ____________________________________________________________________
            | Article                    | Prix Unitaire | Quantité | Taux TVA |
            |----------------------------|---------------|----------|----------|
            | Vistamboire coins nickelés |         10,00 |        2 |     0,20 |
            ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
                                                Montant Hors Taxe :      20,00 €
                                                Montant Total TVA :       4,00 €
                                                Montant Total TTC :      24,00 €
            """;
        // then
        verifyFactureCorrectlyPrinted(facture, vistamboire, expected);
    }
    @Test
    public void given_facture_no_promotion_with_remise_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = getAdresseSecteurGeoAutre();
        Client client = getClientMichelChombier(adresse);
        Facture facture = new Facture(client, FIXED_DATE, 2);
        facture.setRemiseClient(BigDecimal.valueOf(0.5));
        Vistamboire vistamboire = getVistamboire();
        facture.calculate(vistamboire);
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            98 Boulevard Soult
            75012 Paris
            France
    
            ____________________________________________________________________
            | Article                    | Prix Unitaire | Quantité | Taux TVA |
            |----------------------------|---------------|----------|----------|
            | Vistamboire coins nickelés |         10,00 |        2 |     0,20 |
            ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
                                                Remise            :      50,00 %
                                                Montant Hors Taxe :      10,00 €
                                                Montant Total TVA :       2,00 €
                                                Montant Total TTC :      12,00 €
            """;
        // then
        verifyFactureCorrectlyPrinted(facture, vistamboire, expected);
    }
    @Test
    public void given_facture_no_promotion_with_adresse_secteur_geo_maritime_and_vistamboire_label_should_be_Vistamboire_inoxydable() {
        // GIVEN
        Adresse adresse = getAdresseSecteurGeoMaritime();
        Client client = getClientMichelChombier(adresse);
        Facture facture = new Facture(client, FIXED_DATE, 2);
        facture.setRemiseClient(BigDecimal.valueOf(0.5));
        Vistamboire vistamboire = getVistamboire();
        facture.calculate(vistamboire);
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            52 rue Lord Kitchener
            76600 Le Havre
            France
    
            ____________________________________________________________________
            | Article                    | Prix Unitaire | Quantité | Taux TVA |
            |----------------------------|---------------|----------|----------|
            | Vistamboire inoxydable     |         10,00 |        2 |     0,20 |
            ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
                                                Remise            :      50,00 %
                                                Montant Hors Taxe :      10,00 €
                                                Montant Total TVA :       2,00 €
                                                Montant Total TTC :      12,00 €
            """;
        // then
        verifyFactureCorrectlyPrinted(facture, vistamboire, expected);
    }
    @Test
    public void given_facture_promotion_10percent_with_remise_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = getAdresseSecteurGeoAutre();
        Client client = getClientMichelChombier(adresse);
        Facture facture = new Facture(client, FIXED_DATE, 2);
        facture.setRemiseClient(BigDecimal.valueOf(0.5));
        Promotion promotion = new Promotion(LOWER_BOUND, UPPER_BOUND, "Mardi Gras", null, BigDecimal.valueOf(0.1),true);
        facture.getPromotions().add(promotion);
        Vistamboire vistamboire = getVistamboire();
        facture.calculate(vistamboire);
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            98 Boulevard Soult
            75012 Paris
            France
    
            ____________________________________________________________________
            | Article                    | Prix Unitaire | Quantité | Taux TVA |
            |----------------------------|---------------|----------|----------|
            | Vistamboire coins nickelés |         10,00 |        2 |     0,20 |
            ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
            Promotions
            ____________________________________________________________________
            | Désignation                              | Montant | Pourcentage |
            |------------------------------------------|---------|-------------|
            | Mardi Gras                               |         |     10,00 % |
            ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾
                                                Remise            :      50,00 %
                                                Montant Hors Taxe :       9,00 €
                                                Montant Total TVA :       1,80 €
                                                Montant Total TTC :      10,80 €
            """;
        // then
        verifyFactureCorrectlyPrinted(facture, vistamboire, expected);
    }

    private Adresse getAdresseSecteurGeoMaritime() {
        return new Adresse(1L, null, "52 rue Lord Kitchener", null, "76600", "Le Havre", "France");
    }
    private Adresse getAdresseSecteurGeoAutre() {
        return new Adresse(1L, null, "98 Boulevard Soult", null, "75012", "Paris", "France");
    }
    private Vistamboire getVistamboire() {
        return new Vistamboire(
                BigDecimal.TEN,
                new BigDecimal(0.2d),
                new BigDecimal(1),
                LOWER_BOUND,
                UPPER_BOUND);
    }
    private Client getClientMichelChombier(Adresse adresse) {
        return new Client(1L, "Chombier", "Michel", adresse);
    }
    private void verifyFactureCorrectlyPrinted(Facture facture, Vistamboire vistamboire, String expected) {
        String actual = printer.printFacture(facture, vistamboire);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
