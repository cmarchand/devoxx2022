package com.oxiane.formation.devoxx22.refacto.services.business.impl;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Promotion;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.business.FactureBusiness;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import com.oxiane.formation.devoxx22.refacto.services.jpa.ClientRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.FactureRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.PromotionRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.VistamboireRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

public class FactureBusinessImpl implements FactureBusiness {
    @Autowired
    FactureRepository repository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    VistamboireRepository vistamboireRepository;

    @Autowired
    DatabaseValuesExtractor databaseValuesExtractor;

    @Autowired
    PrixUnitCalculateur prixUnitCalculateur;

    @Autowired
    PromotionRepository promotionRepository;

    @Autowired
    FacturePrinter printer;

    @Override
    public Facture createAndSaveFacture(Long clientId, int qte) {
        Client client = findClientById(clientId);
        Facture facture = new Facture(client, getCurrentDate(), qte);
        Vistamboire vistamboire = getVistamboireForFacture(facture);
        facture.setRemiseClient(calculateRemiseClientForFacture(facture));
        facture.calculate(vistamboire);
        applyPromotionsToFacture(facture);
        facture.calculate(vistamboire);
        return repository.save(facture);
    }

    @Override
    public Optional<Facture> getFacture(Long id) {
        return repository
                .findById(id);
    }

    @Override
    public Iterable<Facture> getFactures() {
        return repository.findAll();
    }

    @Override
    public String printFacture(Long id) {
        Facture facture = getFacture(id).orElseThrow();
        Vistamboire vistamboire = vistamboireRepository.findByValidAtDate(facture.getDate());
        return printer.printFacture(facture, vistamboire);
    }

    private Client findClientById(Long clientId) {
        return clientRepository
                .findById(clientId)
                .orElseThrow();
    }


    private Vistamboire getVistamboireForFacture(Facture facture) {
        Vistamboire vistamboire = vistamboireRepository.findByValidAtDate(facture.getDate());
        vistamboire.setPrixUnitaireHT(prixUnitCalculateur.calculatePrixUnit(vistamboire, facture.getClient()));
        return vistamboire;
    }

    private BigDecimal calculateRemiseClientForFacture(Facture facture) {
        int quantiteDejaCommandeeCetteAnnee = databaseValuesExtractor.getQuantiteDejaCommandeeCetteAnnee(
                facture.getClient().getId(),
                facture.getDate());
        return prixUnitCalculateur.calculateRemiseClient(
                facture.getClient(),
                quantiteDejaCommandeeCetteAnnee,
                facture.getQte());
    }
    private void applyPromotionsToFacture(Facture facture) {
        List<Promotion> availablePromotions = promotionRepository.findPromotionsValidAtDate(facture.getDate());
        if(thereIsNoExclusivePromotionIn(availablePromotions)) {
            facture.getPromotions().addAll(availablePromotions);
        } else {
            Promotion bestPromotion = getBestExclusivePromotionForFacture(facture, availablePromotions);
            facture.getPromotions().add(bestPromotion);
        }
    }
    private Promotion getBestExclusivePromotionForFacture(Facture facture, List<Promotion> availablePromotions) {
        record PromotionCalculee(Promotion promotion, BigDecimal montant) {};
        return availablePromotions.stream()
                .filter(Promotion::isExclusive)
                .map(promotion -> new PromotionCalculee(promotion, getRemiseAmountOfPromotionAppliedTo(promotion, facture)))
                .max((pc1, pc2) -> pc1.montant.subtract(pc2.montant).signum())
                .get()
                .promotion;
    }

    private boolean thereIsNoExclusivePromotionIn(List<Promotion> availablePromotions) {
        return availablePromotions
                .stream()
                .noneMatch(Promotion::isExclusive);
    }

    private BigDecimal getRemiseAmountOfPromotionAppliedTo(Promotion promotion, Facture facture) {
        if(promotion.getMontantRemise()!=null) {
            return promotion.getMontantRemise();
        } else {
            return facture.getTotalHT().multiply(promotion.getPourcentageRemise());
        }
    }
    private GregorianCalendar getCurrentDate() {
        return new GregorianCalendar();
    }
}
