package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.services.jpa.AdresseRepository;
import com.oxiane.formation.devoxx22.refacto.services.jpa.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private static Logger LOGGER = LoggerFactory.getLogger(ClientController.class);
    @Autowired
    ClientRepository repository;

    @Autowired
    private AdresseRepository adresseRepository;

    @GetMapping("/{id}")
    public Client getClient(@PathVariable(name = "id") Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client inconnu: "+id)
                );
    }

    @GetMapping("/")
    public Iterable<Client> getClients(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom) {
        LOGGER.info("getClients({}, {})", nom, prenom);
        if(prenom!=null && !prenom.isEmpty()) {
            return repository.findClientsByNomAndPrenom(nom, prenom);
        } else if(nom!=null && !nom.isEmpty()){
            return repository.findClientsByNom(nom);
        } else {
            return repository.findAll();
        }
    }

    @PostMapping("/")
    public Client createClient(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam Long adresseId) {
        Adresse adresse = adresseRepository
                .findById(adresseId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adresse inconnue: "+adresseId)
                );
        Client client = new Client(nom, prenom, adresse);
        return repository.save(client);
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
