package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jpa.ClientRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.FactureRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.VistamboireRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


@RestController
@RequestMapping("/api/factures")
public class FactureController {
    private static final String CR = System.getProperty("line.separator");
    private static final String TAB = "\t";
    @Autowired
    FactureRepository repository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    VistamboireRepository vistamboireRepository;

    private Logger LOGGER = LoggerFactory.getLogger(FactureController.class);

    @PostMapping("/")
    public Facture createFacture(
            @RequestParam Long clientId
        ) {
        Client client = clientRepository
                .findById(clientId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client inconnu: "+clientId));
        Facture facture = new Facture(client, new GregorianCalendar());
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
}
