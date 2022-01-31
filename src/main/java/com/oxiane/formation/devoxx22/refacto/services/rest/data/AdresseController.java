package com.oxiane.formation.devoxx22.refacto.services.rest.data;

import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import com.oxiane.formation.devoxx22.refacto.services.jpa.AdresseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdresseController {
    @Autowired
    AdresseRepository repository;

    @GetMapping("/adresse/{id}")
    public Adresse getAdresse(@PathVariable(name = "id") Long id) {
        return repository.findById(id).orElseThrow();
    }

    @DeleteMapping("/adresse/{id}")
    public void deleteAdresse(@PathVariable(name = "id") Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/adresse")
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

    @PutMapping("/adresse/{id}")
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
