package com.oxiane.formation.devoxx22.refacto.services.business.impl;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.helpers.impl.FacturePrinterImpl;
import com.oxiane.formation.devoxx22.refacto.model.*;
import com.oxiane.formation.devoxx22.refacto.services.business.FactureBusiness;
import com.oxiane.formation.devoxx22.refacto.services.business.impl.FactureBusinessImpl;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import com.oxiane.formation.devoxx22.refacto.services.jpa.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FactureBusinessTest {

    private static final SecteurGeographique SECTEUR_GEO_MARITIME = new SecteurGeographique(SecteurGeographique.NOM_MARITIME, BigDecimal.valueOf(2.0));
    private static final SecteurGeographique SECTEUR_GEO_AUTRE = new SecteurGeographique(SecteurGeographique.NOM_AUTRE, BigDecimal.ONE);
    private static final Adresse ADRESSE_LH = new Adresse(10l,null,"10 rue de Paris", null, "76600", "Le Havre", "France");
    private static final Adresse ADRESSE_PARIS = new Adresse(11l,null,"10 rue du Havre", null, "75009", "Paris", "France");
    private static final Client CLIENT_LH = new Client(20l, "Marchand", "Christophe", ADRESSE_LH);
    private static final Client CLIENT_PARIS = new Client(21l, "Martin", "Sophie", ADRESSE_PARIS);
    private static final Client CLIENT_PROFESSIONNEL = new Client(22l, "Martin", "Sophie", Client.TYPE_PROFESSIONNEL, ADRESSE_PARIS);
    private static final Vistamboire VISTAMBOIRE = new Vistamboire(BigDecimal.TEN, BigDecimal.valueOf(0.2), BigDecimal.ONE, new GregorianCalendar(0001, 0, 1), new GregorianCalendar(3000, 12, 31));
    private static final Facture FACTURE = new Facture(1l,CLIENT_PARIS, new GregorianCalendar(), BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN);

    private Calendar DATE_1 = new GregorianCalendar(1972,10,21);
    private Calendar DATE_2 = new GregorianCalendar(1994,07,24);

    private static int promotionCounter = 0;

    @MockBean
    DatabaseValuesExtractor databaseValuesExtractor;
    @MockBean
    ClientRepository clientRepository;
    @MockBean
    AdresseRepository adresseRepository;
    @MockBean
    FactureRepository factureRepository;
    @Autowired
    PromotionRepository promotionRepository;
    @MockBean
    VistamboireRepository vistamboireRepository;
    @Autowired
    PrixUnitCalculateur prixUnitCalculateur;
    @MockBean
    FacturePrinter facturePrinter;
    @Configuration
    static class Config {
        @Bean
        public FacturePrinter facturePrinter() { return new FacturePrinterImpl(); }
        @Bean
        public PrixUnitCalculateur prixUnitCalculateur() { return Mockito.mock(PrixUnitCalculateur.class); }
        @Bean
        public PromotionRepository promotionRepository() { return Mockito.mock(PromotionRepository.class); }
        @Bean
        public FactureBusiness factureBusiness() { return new FactureBusinessImpl(); }
    }

    @Autowired
    FactureBusiness factureBusiness;

    @BeforeEach
    public void beforeEach() {
        Mockito.when(databaseValuesExtractor.getSecteurGeographiqueByDepartement("76")).thenReturn(SECTEUR_GEO_MARITIME);
        Mockito.when(databaseValuesExtractor.getSecteurGeographiqueByDepartement("75")).thenReturn(SECTEUR_GEO_AUTRE);
        Mockito.when(adresseRepository.findById(10l)).thenReturn(ADRESSE_LH);
        Mockito.when(adresseRepository.findById(11l)).thenReturn(ADRESSE_PARIS);
        Mockito.when(clientRepository.findById(20l)).thenReturn(Optional.of(CLIENT_LH));
        Mockito.when(clientRepository.findById(21l)).thenReturn(Optional.of(CLIENT_PARIS));
        Mockito.when(clientRepository.findById(22l)).thenReturn(Optional.of(CLIENT_PROFESSIONNEL));
        Mockito.when(vistamboireRepository.findByValidAtDate(Mockito.any())).thenReturn(createNewVistamboire());
        Mockito.when(factureRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
    }

    @Test
    @DirtiesContext
    public void given_secteur_geo_autre_date_1_when_create_facture_should_have_no_promotion() {
        // Given
        Mockito.when(promotionRepository.findPromotionsValidAtDate(DATE_1)).thenReturn(Collections.emptyList());
        Mockito
                .when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any()))
                .thenAnswer(
                        invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT()
                );
        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.ZERO);
        // When
        Facture actual = factureBusiness.createAndSaveFacture(21l,1);
        // Then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getClient()).isEqualTo(CLIENT_PARIS);
        softAssertions.assertThat(actual.getClient().getType()).isEqualTo(Client.TYPE_PARTICULIER);
        softAssertions.assertThat(actual.getRemiseClient()).isEqualTo(BigDecimal.ZERO);
        softAssertions.assertThat(actual.getPromotions()).isEmpty();
        softAssertions.assertThat(actual.getTotalHT()).isEqualTo(BigDecimal.TEN);
        softAssertions.assertThat(actual.getTotalTVA()).isEqualTo(BigDecimal.valueOf(2.0));
        softAssertions.assertThat(actual.getTotalTTC()).isEqualTo(BigDecimal.valueOf(12.0));
        softAssertions.assertAll();
    }
    @Test
    @DirtiesContext
    public void given_secteur_geo_autre_date_1_qte_50_when_create_facture_should_have_remise_client() {
        // Given
        Mockito.when(promotionRepository.findPromotionsValidAtDate(DATE_1)).thenReturn(Collections.emptyList());
        Mockito
                .when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any()))
                .thenAnswer(
                        invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT()
                );
        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.valueOf(0.5));
        // When
        Facture actual = factureBusiness.createAndSaveFacture(21l,1);
        // Then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getClient()).isEqualTo(CLIENT_PARIS);
        softAssertions.assertThat(actual.getClient().getType()).isEqualTo(Client.TYPE_PARTICULIER);
        softAssertions.assertThat(actual.getRemiseClient()).isEqualTo(BigDecimal.valueOf(0.5));
        softAssertions.assertThat(actual.getPromotions()).isEmpty();
        Percentage percentage = Percentage.withPercentage(0.01);
        softAssertions.assertThat(actual.getTotalHT()).isCloseTo(BigDecimal.valueOf(5.0), percentage);
        softAssertions.assertThat(actual.getTotalTVA()).isCloseTo(BigDecimal.valueOf(1.0), percentage);
        softAssertions.assertThat(actual.getTotalTTC()).isCloseTo(BigDecimal.valueOf(6.00), percentage);
        softAssertions.assertAll();
    }
    @Test
    @DirtiesContext
    public void given_secteur_geo_autre_date_2_client_professionnel_when_create_facture_should_have_prix_unit_7() {
        // Given
        Vistamboire currentVistamboire = vistamboireRepository.findByValidAtDate(Calendar.getInstance());
        Mockito.when(promotionRepository.findPromotionsValidAtDate(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(prixUnitCalculateur.calculatePrixUnit(currentVistamboire, CLIENT_PROFESSIONNEL)).thenAnswer(invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT().multiply(BigDecimal.valueOf(0.7)));
        Mockito.when(prixUnitCalculateur.calculateRemiseClient(CLIENT_PROFESSIONNEL, 0, 1)).thenReturn(BigDecimal.ZERO);
        // When
        Facture actual = factureBusiness.createAndSaveFacture(22l,1);
        // Then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getPromotions().size()).isEqualTo(0);
        softAssertions.assertThat(actual.getTotalHT()).isEqualTo(BigDecimal.valueOf(7.0));
        softAssertions.assertAll();
    }
    @Test
    public void given_secteur_geo_autre_date_2_when_create_facture_should_have_one_promotion_10_percent() {
        // Given
        Mockito.when(promotionRepository.findPromotionsValidAtDate(Mockito.any())).thenReturn(Arrays.asList(
                createPromotionPercentAround(DATE_2, true)
        ));
        Mockito.when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any())).thenAnswer(invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT());
        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.ZERO);
        // When
        Facture actual = factureBusiness.createAndSaveFacture(21l,1);
        // Then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getPromotions().size()).isEqualTo(1);
        softAssertions.assertThat(actual.getTotalHT()).isEqualTo(BigDecimal.valueOf(9.0));
        softAssertions.assertAll();
    }
    @Test
    public void given_secteur_geo_autre_date_2_when_create_facture_should_have_one_promotion_3_euros() {
        // Given
        Mockito.when(promotionRepository.findPromotionsValidAtDate(Mockito.any())).thenReturn(Arrays.asList(
                createPromotionAmountAround(DATE_2, true)
        ));
        Mockito.when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any())).thenAnswer(invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT());
        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.ZERO);
        // When
        Facture actual = factureBusiness.createAndSaveFacture(21l,1);
        // Then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getPromotions().size()).isEqualTo(1);
        softAssertions.assertThat(actual.getPromotions().get(0).getMontantRemise()).isEqualTo(BigDecimal.valueOf(3.0));
        softAssertions.assertThat(actual.getTotalHT()).isEqualTo(BigDecimal.valueOf(7.0));
        softAssertions.assertAll();
    }
    @Test
    public void given_secteur_geo_autre_and_4_promotions_with_exclusive_facture_should_have_first_promotion_with_best_amount() {
        // Given
        List<Promotion> promotions = Arrays.asList(
                createPromotionAmountAround(DATE_2, 1.0, true),
                createPromotionAmountAround(DATE_2, 3.0, true),
                createPromotionAmountAround(DATE_2, 2.0, true),
                createPromotionAmountAround(DATE_2, 3.0, true)
        );
        Mockito.when(promotionRepository.findPromotionsValidAtDate(Mockito.any())).thenReturn(promotions);
        Mockito.when(prixUnitCalculateur.calculatePrixUnit(Mockito.any(), Mockito.any())).thenAnswer(invocationOnMock -> ((Vistamboire)invocationOnMock.getArguments()[0]).getPrixUnitaireHT());
        Mockito.when(prixUnitCalculateur.calculateRemiseClient(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(BigDecimal.ZERO);
        // When
        Facture actual = factureBusiness.createAndSaveFacture(21l,4);
        // Then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.getPromotions().size()).isEqualTo(1);
        softAssertions.assertThat(actual.getPromotions().get(0).getMontantRemise()).isEqualTo(BigDecimal.valueOf(3.0));
        softAssertions.assertThat(actual.getPromotions().get(0)).isEqualTo(promotions.get(1));
        softAssertions.assertAll();
    }
    @Test
    public void when_getFactures_repository_getFactures_should_be_call_once() {
        Mockito.when(factureRepository.findAll()).thenReturn(Collections.emptyList());
        factureBusiness.getFactures();
        Mockito.verify(factureRepository, Mockito.times(1)).findAll();
    }
    @Test
    public void when_getFacture_repository_getFacture_should_be_call_once() {
        Mockito.when(factureRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
        Throwable thrown = catchThrowable(() -> factureBusiness.getFacture(1l));
        Assertions.assertThat(thrown).isInstanceOf(ResponseStatusException.class);
        Mockito.verify(factureRepository, Mockito.times(1)).findById(1l);
    }
    @Test
    public void when_print_facture_printer_print_should_be_call() {
        Mockito.when(factureRepository.findById(1l)).thenReturn(Optional.of(FACTURE));
        Mockito.when(vistamboireRepository.findByValidAtDate(Mockito.any())).thenReturn(VISTAMBOIRE);
        Mockito.when(facturePrinter.printFacture(FACTURE, VISTAMBOIRE)).thenReturn("facture");
        factureBusiness.printFacture(1l);
        Mockito.verify(facturePrinter, Mockito.times(1)).printFacture(FACTURE, VISTAMBOIRE);
    }

    private Promotion createPromotionPercentAround(Calendar date, boolean exclusive) {
        return createPromotionPercentAround(date, 0.1, exclusive);
    }
    private Promotion createPromotionPercentAround(Calendar date, double amount, boolean exclusive) {
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
                BigDecimal.valueOf(amount),
                exclusive);
    }
    private Promotion createPromotionAmountAround(Calendar date, boolean exclusive) {
        return createPromotionAmountAround(date, 3.0, exclusive);
    }
    private Promotion createPromotionAmountAround(Calendar date, double percent, boolean exclusive) {
        Calendar dateDebut = (Calendar) date.clone();
        dateDebut.set(Calendar.MONTH, dateDebut.get(Calendar.MONTH)-1);
        Calendar dateFin = (Calendar) date.clone();
        dateFin.set(Calendar.MONTH, dateFin.get(Calendar.MONTH)+1);
        return new Promotion(
                1l,
                dateDebut,
                dateFin,
                "Promotion surprise "+promotionCounter++,
                BigDecimal.valueOf(percent),
                null,
                exclusive);
    }
    private Vistamboire createNewVistamboire() {
        return new Vistamboire(
                BigDecimal.TEN,
                BigDecimal.valueOf(0.2),
                BigDecimal.ONE,
                new GregorianCalendar(0001, 0, 1),
                new GregorianCalendar(3000, 12, 31)
        );
    }
}
