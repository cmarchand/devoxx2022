package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.helpers.PrixUnitCalculateur;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Promotion;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jdbc.DatabaseValuesExtractor;
import com.oxiane.formation.devoxx22.refacto.services.jpa.ClientRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.FactureRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.PromotionRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.VistamboireRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;


@RestController
@RequestMapping("/api/factures")
@Tag(name="Factures", description = "Gestion de la facturation")
public class FactureController {
    private static final String CR = System.getProperty("line.separator");
    private static final String TAB = "\t";
    @Autowired
    FactureRepository repository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    VistamboireRepository vistamboireRepository;

    @Autowired
    FacturePrinter printer;

    @Autowired
    DatabaseValuesExtractor databaseValuesExtractor;

    @Autowired
    PrixUnitCalculateur prixUnitCalculateur;

    @Autowired
    PromotionRepository promotionRepository;

    private Logger LOGGER = LoggerFactory.getLogger(FactureController.class);

    @PostMapping("/")
    public Facture createFacture(
            @RequestParam Long clientId,
            @RequestParam(required = false, defaultValue = "1") int qte) {
        Client client = findClientById(clientId);
        Facture facture = new Facture(client, new GregorianCalendar(), qte);
        Vistamboire vistamboire = getVistamboireForFacture(client, facture);
        int qteDejaAchetee = databaseValuesExtractor.getQuantiteDejaCommandeeCetteAnnee(clientId, facture.getDate());
        facture.setRemiseClient(prixUnitCalculateur.calculateRemiseClient(client, qteDejaAchetee, qte));
        List<Promotion> availablePromotions = promotionRepository.findPromotionsValidAtDate(facture.getDate());
        // on regarde si il y a des promotions exclusives, dans ce cas on ne garde que celles-là
        List<Promotion> exclusivePromotions = availablePromotions
                .stream()
                .filter(Promotion::isExclusive)
                .toList();
        if(exclusivePromotions.isEmpty()) {
            facture.getPromotions().addAll(availablePromotions);
        } else {
            facture.calculate(vistamboire);
            Promotion bestPromotion = null;
            BigDecimal bestPromotionAmount = BigDecimal.ZERO;
            for(Promotion promotion: exclusivePromotions) {
                BigDecimal currentPromotionAmount = getRemiseAmountOfPromotionAppliedTo(promotion, facture);
                if (currentPromotionAmount.compareTo(bestPromotionAmount) > 0) {
                    bestPromotion = promotion;
                    bestPromotionAmount = currentPromotionAmount;
                }
            }
            facture.getPromotions().add(bestPromotion);
        }
        facture.calculate(vistamboire);
        return repository.save(facture);
    }

    private Vistamboire getVistamboireForFacture(Client client, Facture facture) {
        Vistamboire vistamboire = vistamboireRepository.findByValidAtDate(facture.getDate());
        vistamboire.setPrixUnitaireHT(prixUnitCalculateur.calculatePrixUnit(vistamboire, client));
        return vistamboire;
    }

    private Client findClientById(Long clientId) {
        return clientRepository
                .findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client inconnu: " + clientId));
    }

    private BigDecimal getRemiseAmountOfPromotionAppliedTo(Promotion promotion, Facture facture) {
        if(promotion.getMontantRemise()!=null) {
            return promotion.getMontantRemise();
        } else {
            return facture.getTotalHT().multiply(promotion.getPourcentageRemise());
        }
    }

    @GetMapping("/{id}")
    public Facture getFacture(@PathVariable Long id) {
        LOGGER.info("getFacture({})", id);
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture inconnue: " + id)
                );
    }

    @GetMapping("/")
    public Iterable<Facture> getFactures() {
        LOGGER.info("getFactures()");
        return repository.findAll();
    }

    @GetMapping(value = "/{id}/print", produces = MediaType.TEXT_PLAIN_VALUE)
    public String printFacture(@PathVariable Long id) {
        LOGGER.info("printFacture({})", id);
        Facture facture = getFacture(id);
        Vistamboire vistamboire = vistamboireRepository.findByValidAtDate(facture.getDate());
        return printer.printFacture(facture, vistamboire);
    }
}
