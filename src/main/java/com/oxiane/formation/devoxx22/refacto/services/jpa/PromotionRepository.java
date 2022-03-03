package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Promotion;
import org.springframework.data.repository.CrudRepository;

public interface PromotionRepository extends CrudRepository<Promotion, Long>, PromotionRepositoryCustom {
}
