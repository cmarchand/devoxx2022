package com.oxiane.formation.devoxx22.refacto.helpers;

import com.oxiane.formation.devoxx22.refacto.helpers.impl.FacturePrinterImpl;
import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FacturePrinterTest {
    // Warning : Monthes are 0-based !!!
    private static final Calendar FIXED_DATE= new GregorianCalendar(2022, 0, 31);
    private static final Calendar LOWER_BOUND = new GregorianCalendar(2022, 0, 1);
    private static final Calendar UPPER_BOUND = new GregorianCalendar(2022, 5, 1);

    @Test
    public void given_facture_with_qte_1_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = new Adresse(1L, null, "98 avenue du Général Leclerc", null, "92100", "Boulogne Billancourt", "France");
        Client client = new Client(1L,"Chombier", "Michel", adresse);
        Facture facture = new Facture(client, FIXED_DATE);
        Vistamboire vistamboire = new Vistamboire(
                BigDecimal.TEN,
                new BigDecimal(0.2d),
                new BigDecimal(1),
                LOWER_BOUND,
                UPPER_BOUND);
        facture.calculate(vistamboire);
        FacturePrinterImpl printer = new FacturePrinterImpl();
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            98 avenue du Général Leclerc
            92100 Boulogne Billancourt
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
        // WHEN
        String actual = printer.printFacture(facture, vistamboire);

        // THEN
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void given_facture_with_qte_2_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = new Adresse(1L, null, "98 avenue du Général Leclerc", null, "92100", "Boulogne Billancourt", "France");
        Client client = new Client(1L,"Chombier", "Michel", adresse);
        Facture facture = new Facture(client, FIXED_DATE, 2);
        Vistamboire vistamboire = new Vistamboire(
                BigDecimal.TEN,
                new BigDecimal(0.2d),
                new BigDecimal(1),
                LOWER_BOUND,
                UPPER_BOUND);
        facture.calculate(vistamboire);
        FacturePrinterImpl printer = new FacturePrinterImpl();
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            98 avenue du Général Leclerc
            92100 Boulogne Billancourt
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
        // WHEN
        String actual = printer.printFacture(facture, vistamboire);

        // THEN
        Assertions.assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void given_facture_with_remise_and_vistamboire_print_output_shouldbe_full() {
        // GIVEN
        Adresse adresse = new Adresse(1L, null, "98 avenue du Général Leclerc", null, "92100", "Boulogne Billancourt", "France");
        Client client = new Client(1L,"Chombier", "Michel", adresse);
        Facture facture = new Facture(client, FIXED_DATE, 2);
        facture.setRemiseClient(BigDecimal.valueOf(0.5));
        Vistamboire vistamboire = new Vistamboire(
                BigDecimal.TEN,
                new BigDecimal(0.2d),
                new BigDecimal(1),
                LOWER_BOUND,
                UPPER_BOUND);
        facture.calculate(vistamboire);
        FacturePrinterImpl printer = new FacturePrinterImpl();
        String expected = """
            Facture null / 31-01-2022
            
            Michel Chombier
            98 avenue du Général Leclerc
            92100 Boulogne Billancourt
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
        // WHEN
        String actual = printer.printFacture(facture, vistamboire);

        // THEN
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
