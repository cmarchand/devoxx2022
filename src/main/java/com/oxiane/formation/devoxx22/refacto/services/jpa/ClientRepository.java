package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client, Long> {

    public List<Client> findClientsByNom(String nom);
    public List<Client> findClientsByNomAndPrenom(String nom, String prenom);
}
