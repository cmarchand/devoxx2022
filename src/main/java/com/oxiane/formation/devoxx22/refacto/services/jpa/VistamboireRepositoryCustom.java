package com.oxiane.formation.devoxx22.refacto.services.jpa;

import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;

import java.util.Calendar;

public interface VistamboireRepositoryCustom {

    public Vistamboire findByValidAtDate(Calendar validityDate);
}
