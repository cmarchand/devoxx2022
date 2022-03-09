package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.helpers.VerboseDateFormat;
import com.oxiane.formation.devoxx22.refacto.model.Promotion;
import com.oxiane.formation.devoxx22.refacto.services.jpa.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {
    @Autowired
    PromotionRepository repository;

    @Autowired
    VerboseDateFormat apiDateFormatter;

    @GetMapping("/")
    public Iterable<PromotionRecord> getPromotions() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(PromotionRecord::new)
                .toList();
    }

    @GetMapping("{id}")
    public PromotionRecord getPromotion(@PathVariable Long id) {
        return repository.findById(id)
                .stream()
                .map(PromotionRecord::new)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Promotion inconnue: "+id));
    }

    @PostMapping("/")
    public PromotionRecord createPromotion(
            @RequestParam String nom,
            @RequestParam String dateDebut,
            @RequestParam String dateFin,
            @RequestParam(required = false) BigDecimal montantRemise,
            @RequestParam(required = false) BigDecimal pourcentageRemise,
            @RequestParam boolean exclusive) {
        Calendar calDateDebut = Calendar.getInstance();
        Calendar calDateFin = Calendar.getInstance();
        try {
            calDateDebut.setTime(apiDateFormatter.parse(dateDebut));
            calDateFin.setTime(apiDateFormatter.parse(dateFin));
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Le format de date doit être "+apiDateFormatter.getFormat());
        }
        Promotion promotion = new Promotion(calDateDebut, calDateFin, nom, montantRemise, pourcentageRemise, exclusive);
        return new PromotionRecord(repository.save(promotion));
    }

    @GetMapping("/valid/{date}")
    public Iterable<PromotionRecord> getPromotionsValidAt(String date) {
        Calendar calDate = Calendar.getInstance();
        try {
            calDate.setTime(apiDateFormatter.parse(date));
        } catch (ParseException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Le format de date doit être "+apiDateFormatter.getFormat());
        }
        return repository.findPromotionsValidAtDate(calDate)
                .stream()
                .map(PromotionRecord::new)
                .toList();
    }

    public record PromotionRecord(Long id, PeriodeValidite periode, String nom, Remise remise, boolean exclusive) {
        PromotionRecord(Promotion promotion) {
            this(
                    promotion.getId(),
                    new PeriodeValidite(promotion.getDateDebut(), promotion.getDateFin()),
                    promotion.getNom(),
                    new Remise(promotion.getMontantRemise(), promotion.getPourcentageRemise()),
                    promotion.isExclusive());
        }
    }
    public record PeriodeValidite(Calendar dateDebut, Calendar dateFin){};
    public record Remise(BigDecimal montant, BigDecimal pourcentage){};
}
