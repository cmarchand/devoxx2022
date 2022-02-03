package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.helpers.FacturePrinter;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jpa.ClientRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.FactureRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.VistamboireRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.GregorianCalendar;


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

    private Logger LOGGER = LoggerFactory.getLogger(FactureController.class);

    @PostMapping("/")
    public Facture createFacture(
            @RequestParam Long clientId,
            @RequestParam(required = false, defaultValue = "1") int qte
        ) {
        Client client = clientRepository
                .findById(clientId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client inconnu: "+clientId));
        Facture facture = new Facture(client, new GregorianCalendar(), qte);
        Vistamboire vistamboire = vistamboireRepository.findByValidAtDate(facture.getDate());
        facture.calculate(vistamboire);
        return repository.save(facture);
    }

    @GetMapping("/{id}")
    public Facture getFacture(@PathVariable Long id) {
        LOGGER.info("getFacture({})", id);
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture inconnue: "+id)
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
