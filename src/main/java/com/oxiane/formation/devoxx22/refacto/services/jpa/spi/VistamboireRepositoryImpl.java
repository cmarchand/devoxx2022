package com.oxiane.formation.devoxx22.refacto.services.jpa.spi;

import com.oxiane.formation.devoxx22.refacto.model.Vistamboire;
import com.oxiane.formation.devoxx22.refacto.services.jpa.VistamboireRepositoryCustom;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Calendar;
import java.util.List;

@Component
public class VistamboireRepositoryImpl implements VistamboireRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Vistamboire findByValidAtDate(Calendar validityDate) {
        Query query = em.createQuery(
                "select v from Vistamboire v where v.validSince <= :date and :date < v.validUntil order by v.validUntil desc",
                Vistamboire.class);
        query.setParameter("date", validityDate, TemporalType.DATE);
        List<Vistamboire> liste = query.getResultList();
        if(liste==null || liste.isEmpty()) return null;
        return liste.get(0);
    }
}
