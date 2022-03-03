package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Promotion;

import java.util.Calendar;
import java.util.List;

public interface PromotionRepositoryCustom {
    public List<Promotion> findPromotionsValidAtDate(Calendar date);
}
