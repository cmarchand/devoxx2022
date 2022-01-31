package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Adresse;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdresseRepository extends CrudRepository<Adresse, Long> {

    public List<Adresse> findByCodepostal(String codePostal);
    public Adresse findById(long id);
    public List<Adresse> findByPaysAndCodepostal(String pays, String codePostal);
}
