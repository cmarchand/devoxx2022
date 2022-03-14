package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.model.Facture;
import com.oxiane.formation.devoxx22.refacto.services.business.FactureBusiness;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factures")
@Tag(name="Factures", description = "Gestion de la facturation")
public class FactureController {
    private static final String CR = System.getProperty("line.separator");
    private static final String TAB = "\t";

    @Autowired
    FactureBusiness factureBusiness;

    private Logger LOGGER = LoggerFactory.getLogger(FactureController.class);

    @PostMapping("/")
    public Facture createFacture(
            @RequestParam Long clientId,
            @RequestParam(required = false, defaultValue = "1") int qte) {
        return factureBusiness.createAndSaveFacture(clientId, qte);
    }

    @GetMapping("/{id}")
    public Facture getFacture(@PathVariable Long id) {
        return factureBusiness.getFacture(id);
    }

    @GetMapping("/")
    public Iterable<Facture> getFactures() {
        return factureBusiness.getFactures();
    }

    @GetMapping(value = "/{id}/print", produces = MediaType.TEXT_PLAIN_VALUE)
    public String printFacture(@PathVariable Long id) {
        return factureBusiness.printFacture(id);
    }
}
