package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.services.jpa.AdresseRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/adresses")
@Tag(name="adresses", description = "Manipulation des adresses")
public class AdresseController {
    @Autowired
    AdresseRepository repository;

    @GetMapping("/")
    public Iterable<Adresse> getAdresses() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "404", description = "Adresse inconnue")
    public Adresse getAdresse(
            @PathVariable(name = "id")
            @Parameter(name = "id", description = "Identifiant de l'adresse") Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adresse inconnue: "+id)
                );
    }

    @DeleteMapping("/{id}")
    public void deleteAdresse(@PathVariable(name = "id") Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/")
    public Adresse createAdresse(
            @RequestParam(required = false) String adresse1,
            @RequestParam(required = true) String adresse2,
            @RequestParam(required = false) String adresse3,
            @RequestParam(required = true) String codePostal,
            @RequestParam(required = true) String ville,
            @RequestParam(required = false, defaultValue = "France") String pays) {
        Adresse source = new Adresse(adresse1, adresse2, adresse3, codePostal, ville, pays);
        return repository.save(source);
    }

    @PutMapping("/{id}")
    public Adresse modifyAdresse(
            @PathVariable(name = "id") Long id,
            Adresse adresse) {
        Adresse actual = repository.findById(id).orElseThrow();
        actual.setAdresse1(adresse.getAdresse1());
        actual.setAdresse2(adresse.getAdresse2());
        actual.setAdresse3(adresse.getAdresse3());
        actual.setCodepostal(adresse.getCodepostal());
        actual.setVille(adresse.getVille());
        actual.setPays(adresse.getPays());
        return repository.save(actual);
    }
}
