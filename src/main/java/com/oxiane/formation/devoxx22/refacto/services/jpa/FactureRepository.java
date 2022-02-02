package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Client;
import com.oxiane.formation.devoxx22.refacto.model.Facture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FactureRepository extends CrudRepository<Facture, Long> {

    public Iterable<Facture> getFacturesByClientId(Long clientId);
    public List<Facture> getFacturesByClient(Client client);
}
