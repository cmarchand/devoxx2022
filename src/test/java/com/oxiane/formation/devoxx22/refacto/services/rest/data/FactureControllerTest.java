package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.helpers.impl.FacturePrinterImpl;
import com.oxiane.formation.devoxx22.refacto.model.*;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import com.oxiane.formation.devoxx22.refacto.services.jpa.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.*;

@SpringJUnitConfig

public class FactureControllerTest {

    private static final SecteurGeographique SECTEUR_GEO_MARITIME = new SecteurGeographique(SecteurGeographique.NOM_MARITIME, BigDecimal.valueOf(2.0));
    private static final SecteurGeographique SECTEUR_GEO_AUTRE = new SecteurGeographique(SecteurGeographique.NOM_AUTRE, BigDecimal.ONE);
    private static final Adresse ADRESSE_LH = new Adresse(10l,null,"10 rue de Paris", null, "76600", "Le Havre", "France");
    private static final Adresse ADRESSE_PARIS = new Adresse(11l,null,"10 rue du Havre", null, "75009", "Paris", "France");
    private static final Client CLIENT_LH = new Client(20l, "Marchand", "Christophe", ADRESSE_LH);
    private static final Client CLIENT_PARIS = new Client(21l, "Martin", "Sophie", ADRESSE_PARIS);
    private static final Vistamboire VISTAMBOIRE = new Vistamboire(BigDecimal.TEN, BigDecimal.valueOf(0.2), BigDecimal.ONE, new GregorianCalendar(0001, 0, 1), new GregorianCalendar(3000, 12, 31));

    private Calendar DATE_1 = new GregorianCalendar(1972,10,21);
    private Calendar DATE_2 = new GregorianCalendar(1994,07,24);

    @MockBean
    DatabaseValuesExtractor databaseValuesExtractor;
    @MockBean
    ClientRepository clientRepository;
    @MockBean
    AdresseRepository adresseRepository;
    @MockBean
    FactureRepository factureRepository;
    @MockBean
    PromotionRepository promotionRepository;
    @MockBean
    VistamboireRepository vistamboireRepository;
    @MockBean
    PrixUnitCalculateur prixUnitCalculateur;
    @Configuration
    static class Config {
        @Bean
        public FactureController factureController() { return new FactureController(); }
        @Bean
        public FacturePrinter facturePrinter() { return new FacturePrinterImpl(); }
    }

    @Autowired
    FactureController controller;

    @BeforeEach
    public void beforeEach() {
        Mockito.when(databaseValuesExtractor.getSecteurGeographiqueByDepartement("76")).thenReturn(SECTEUR_GEO_MARITIME);
        Mockito.when(databaseValuesExtractor.getSecteurGeographiqueByDepartement("75")).thenReturn(SECTEUR_GEO_AUTRE);
        Mockito.when(adresseRepository.findById(10l)).thenReturn(ADRESSE_LH);
        Mockito.when(adresseRepository.findById(11l)).thenReturn(ADRESSE_PARIS);
        Mockito.when(clientRepository.findById(20l)).thenReturn(Optional.of(CLIENT_LH));
        Mockito.when(clientRepository.findById(21l)).thenReturn(Optional.of(CLIENT_PARIS));
        Mockito.when(vistamboireRepository.findByValidAtDate(Mockito.any())).thenReturn(VISTAMBOIRE);
        Mockito.when(factureRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
    }

//    @Test
//    public void given_secteur_geo_autre_date_1_when_create_facture_should_have_no_promotion() {
//        // Given
//        Mockito.when(promotionRepository.findPromotionsValidAtDate(DATE_1)).thenReturn(Collections.emptyList());
//        Mockito.when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any())).thenAnswer(invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT());
//        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.ZERO);
//        // When
//        Facture actual = controller.createFacture(21l,1);
//        // Then
//        SoftAssertions softAssertions = new SoftAssertions();
//        softAssertions.assertThat(actual.getRemiseClient()).isEqualTo(BigDecimal.ZERO);
//        softAssertions.assertThat(actual.getPromotions()).isEmpty();
//        softAssertions.assertThat(actual.getTotalHT()).isEqualTo(BigDecimal.TEN);
//        softAssertions.assertThat(actual.getTotalTVA()).isEqualTo(BigDecimal.valueOf(2.0));
//        softAssertions.assertThat(actual.getTotalTTC()).isEqualTo(BigDecimal.valueOf(12.0));
//        softAssertions.assertAll();
//    }

//    @Test
//    public void given_secteur_geo_autre_date_2_when_create_facture_should_have_one_promotion_10_percent() {
//        // Given
//        Mockito.when(promotionRepository.findPromotionsValidAtDate(Mockito.any())).thenReturn(Arrays.asList(
//                createPromotionAround(DATE_2, true)
//        ));
//        Mockito.when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any())).thenAnswer(invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT());
//        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.ZERO);
//        // When
//        Facture actual = controller.createFacture(21l,1);
//        // Then
//        SoftAssertions softAssertions = new SoftAssertions();
//        softAssertions.assertThat(actual.getPromotions().size()).isEqualTo(1);
//        softAssertions.assertThat(actual.getTotalHT()).isEqualTo(BigDecimal.valueOf(9.0));
//        softAssertions.assertAll();
//    }

    private Promotion createPromotionAround(Calendar date, boolean exclusive) {
        Calendar dateDebut = (Calendar) date.clone();
        dateDebut.set(Calendar.MONTH, dateDebut.get(Calendar.MONTH)-1);
        Calendar dateFin = (Calendar) date.clone();
        dateFin.set(Calendar.MONTH, dateFin.get(Calendar.MONTH)+1);
        return new Promotion(
                1l,
                dateDebut,
                dateFin,
                "Promotion surprise",
                null,
                BigDecimal.valueOf(0.1),
                exclusive);
    }
}
