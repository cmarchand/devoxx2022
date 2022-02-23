package com.oxiane.formation.devoxx22.refacto.helpers;

import com.oxiane.formation.devoxx22.refacto.config.VistamboireTestConfig;
import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;

@SpringJUnitConfig(
        classes = {
                VistamboireTestConfig.class,
        }
)
public class PrixUnitCalculateurTest {
    private Vistamboire vistamboire = new Vistamboire(
            BigDecimal.TEN,
            new BigDecimal(0.2),
            BigDecimal.ONE,
            null,
            null
    );
    private static final Percentage LIMITE = Percentage.withPercentage(0.001);
    @Autowired
    private PrixUnitCalculateur calculateur;

    @Test
    public void given_particulier_secteur_geo_autre_prixUnit_is_10() {
        // Given
        Client client = new Client(
                "Nom",
                "Prenom",
                Client.TYPE_PARTICULIER,
                getAdresseAutre());
        BigDecimal expected = BigDecimal.TEN;

        // When
        BigDecimal actual = calculateur.calculatePrixUnit(vistamboire, client);

        // Then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void given_particulier_secteur_geo_maritime_prixUnit_is_11dot5() {
        // Given
        Client client = new Client(
                "Nom",
                "Prenom",
                Client.TYPE_PARTICULIER,
                getAdresseMaritime());
        BigDecimal expected = BigDecimal.valueOf(11.5);

        // When
        BigDecimal actual = calculateur.calculatePrixUnit(vistamboire, client);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }

    @Test
    public void given_professionnel_secteur_geo_autre_prixUnit_7() {
        // Given
        Client client = new Client(
                "Nom",
                "Prenom",
                Client.TYPE_PROFESSIONNEL,
                getAdresseAutre());
        BigDecimal expected = BigDecimal.valueOf(7);

        // When
        BigDecimal actual = calculateur.calculatePrixUnit(vistamboire, client);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_maritime_autre_prixUnit_8dot05() {
        // Given
        Client client = new Client(
                "Nom",
                "Prenom",
                Client.TYPE_PROFESSIONNEL,
                getAdresseMaritime());
        BigDecimal expected = BigDecimal.valueOf(8.05);

        // When
        BigDecimal actual = calculateur.calculatePrixUnit(vistamboire, client);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_particulier_qte1_qteachetee10_discount_is_0() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PARTICULIER, null);
        int qteDejaAchetee = 10;
        int qte = 1;
        BigDecimal expected = BigDecimal.ZERO;

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_geo_autre_qte1_qteachetee10_discount_is_10() {
        // Given
        Client client = new Client(
                "Nom",
                "Prenom",
                Client.TYPE_PROFESSIONNEL,
                getAdresseAutre());
        int qteDejaAchetee = 10;
        int qte = 1;
        BigDecimal expected = BigDecimal.valueOf(0.1);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_geo_autre_qte1_qteachetee20_discount_is_15() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, getAdresseAutre());
        int qteDejaAchetee = 20;
        int qte = 1;
        BigDecimal expected = BigDecimal.valueOf(0.15);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_geo_autre_qte1_qteachetee50_discount_is_20() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, getAdresseAutre());
        int qteDejaAchetee = 50;
        int qte = 1;
        BigDecimal expected = BigDecimal.valueOf(0.2);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_geo_maritime_qte1_qteachetee15_discount_is_10() {
        // Given
        Client client = new Client(
                "Nom",
                "Prenom",
                Client.TYPE_PROFESSIONNEL,
                getAdresseMaritime());
        int qteDejaAchetee = 15;
        int qte = 1;
        BigDecimal expected = BigDecimal.valueOf(0.1);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_geo_maritime_qte1_qteachetee25_discount_is_17() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, getAdresseMaritime());
        int qteDejaAchetee = 25;
        int qte = 1;
        BigDecimal expected = BigDecimal.valueOf(0.17);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    @Test
    public void given_professionnel_secteur_geo_maritime_qte1_qteachetee40_discount_is_20() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, getAdresseMaritime());
        int qteDejaAchetee = 40;
        int qte = 1;
        BigDecimal expected = BigDecimal.valueOf(0.2);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, LIMITE);
    }
    private Adresse getAdresseAutre() {
        return new Adresse(1l, null, "21 rue des Lavandières Sainte-Opportune", null, "75001", "Paris", "France");
    }
    private Adresse getAdresseMaritime() {
        return new Adresse(2l, null, "120 rue Augustin Normand", null, "76600", "Le Havre", "France");
    }

}