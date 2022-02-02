package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import org.springframework.data.repository.CrudRepository;

public interface VistamboireRepository extends CrudRepository<Vistamboire, Long>, VistamboireRepositoryCustom {

}
