package com.oxiane.formation.devoxx22.refacto.helpers;

import com.oxiane.formation.devoxx22.refacto.helpers.impl.PrixUnitCalculateurImpl;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PrixUnitCalculateurTest {
    private Vistamboire vistamboire = new Vistamboire(
            BigDecimal.TEN,
            new BigDecimal(0.2),
            BigDecimal.ONE,
            null,
            null
    );

    @Test
    public void given_particulier_prixUnit_is_10() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PARTICULIER, null);
        PrixUnitCalculateurImpl calculateur = new PrixUnitCalculateurImpl();
        BigDecimal expected = BigDecimal.TEN;

        // When
        BigDecimal actual = calculateur.calculatePrixUnit(vistamboire, client);

        // Then
        Assertions.assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void given_professionnel_prixUnit_7() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, null);
        PrixUnitCalculateurImpl calculateur = new PrixUnitCalculateurImpl();
        BigDecimal expected = new BigDecimal(7);

        // When
        BigDecimal actual = calculateur.calculatePrixUnit(vistamboire, client);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, Percentage.withPercentage(0.01));
    }
    @Test
    public void given_particulier_qte1_qteachetee10_discount_is_0() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PARTICULIER, null);
        int qteDejaAchetee = 10;
        int qte = 1;
        PrixUnitCalculateurImpl calculateur = new PrixUnitCalculateurImpl();
        BigDecimal expected = BigDecimal.ZERO;

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, Percentage.withPercentage(0.01));
    }
    @Test
    public void given_professionnel_qte1_qteachetee10_discount_is_10() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, null);
        int qteDejaAchetee = 10;
        int qte = 1;
        PrixUnitCalculateurImpl calculateur = new PrixUnitCalculateurImpl();
        BigDecimal expected = new BigDecimal(0.1);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, Percentage.withPercentage(0.01));
    }
    @Test
    public void given_professionnel_qte1_qteachetee20_discount_is_15() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, null);
        int qteDejaAchetee = 20;
        int qte = 1;
        PrixUnitCalculateurImpl calculateur = new PrixUnitCalculateurImpl();
        BigDecimal expected = new BigDecimal(0.15);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, Percentage.withPercentage(0.01));
    }
    @Test
    public void given_professionnel_qte1_qteachetee50_discount_is_20() {
        // Given
        Client client = new Client("Nom", "Prenom", Client.TYPE_PROFESSIONNEL, null);
        int qteDejaAchetee = 50;
        int qte = 1;
        PrixUnitCalculateurImpl calculateur = new PrixUnitCalculateurImpl();
        BigDecimal expected = new BigDecimal(0.2);

        // When
        BigDecimal actual = calculateur.calculateRemiseClient(client, qteDejaAchetee, qte);

        // Then
        Assertions.assertThat(actual).isCloseTo(expected, Percentage.withPercentage(0.01));
    }
}
